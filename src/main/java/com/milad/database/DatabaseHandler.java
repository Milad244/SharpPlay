package com.milad.database;
import com.milad.core.Song;
import com.milad.core.Playlist;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {
    private static final String DB_url = "jdbc:sqlite:User_Data/SharpPlay_DB";
    private static Connection conn = null;
    private static Statement stmt = null;
    public static DatabaseHandler handler;

    private static final String SONG_TABLE_NAME = "SONG_TABLE";
    private static final String PLAYLIST_TABLE_NAME = "PLAYLIST_TABLE";
    private static final String PLAYLIST_SONG_TABLE_NAME = "PLAYLIST_SONG_TABLE";

    /**
     * Returns database handler which is used to run my database's methods
     * @return returns a new handler if a database handler has not been initiated, else returns the one already initiated
     */
    public static DatabaseHandler getHandler(){
        if(handler == null){
            handler = new DatabaseHandler();
        }
        return handler;
    }

    /**
     * Runs methods that initiate the database
     */
    private DatabaseHandler() {
        connect();
        initSongTable();
        initPlaylistTable();
        initPlaylistSongTable();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection(DB_url);
            System.out.println("Connection Successful");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection Failed");
        }
    }

    private void initSongTable() {
        String qu = "CREATE TABLE IF NOT EXISTS " + SONG_TABLE_NAME + " ("
                + "	id INTEGER PRIMARY KEY,"
                + "	file VARCHAR(255),"
                + "	title VARCHAR(255),"
                + "	author VARCHAR(255),"
                + " added DATE,"
                + " color INTEGER"
                + ");";
        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(SONG_TABLE_NAME + " made/exists");
        } else {
            System.out.println(SONG_TABLE_NAME +  " query failed");
        }
    }

    private void initPlaylistTable() {
        String qu = "CREATE TABLE IF NOT EXISTS " + PLAYLIST_TABLE_NAME + " ("
                + "	id INTEGER PRIMARY KEY,"
                + "	name VARCHAR(255),"
                + "	icon_file VARCHAR(255),"
                + "	added DATE"
                + ");";
        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(PLAYLIST_TABLE_NAME + " made/exists");
        } else {
            System.out.println(PLAYLIST_TABLE_NAME + " query failed");
        }
    }

    private void initPlaylistSongTable() {
        String qu = "CREATE TABLE IF NOT EXISTS " + PLAYLIST_SONG_TABLE_NAME + " ("
                + "	playlist_id INTEGER,"
                + "	song_id INTEGER"
                + ");";
        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(PLAYLIST_SONG_TABLE_NAME + " made/exists");
        } else {
            System.out.println(PLAYLIST_SONG_TABLE_NAME + " query failed");
        }
    }

    public void insertSong(Song song) {
        String qu = "INSERT INTO " + SONG_TABLE_NAME + " (file, title, author, added, color) VALUES ('" +
                song.getFile() + "', '" +
                song.getTitle() + "', '" +
                song.getAuthor() + "', '" +
                song.getAdded() + "', " +
                song.getColor() +
                ");";

        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println("Song inserted");
        } else {
            System.out.println("Failed to insert song");
        }
    }

    public void insertPlaylist(Playlist playlist) {
        String qu = "INSERT INTO " + PLAYLIST_TABLE_NAME + " (name, icon_file, added) VALUES ('" +
                playlist.getName() + "', '" +
                playlist.getIconFile() + "', '" +
                playlist.getAdded() +
                "');";

        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println("Playlist inserted");
        } else {
            System.out.println("Failed to insert playlist");
        }
    }

    public void insertPlaylistSong(Playlist playlist, Song song) {
        String qu = "INSERT INTO " + PLAYLIST_SONG_TABLE_NAME + " (playlist_id, song_id) VALUES ('" +
                playlist.getId() + "', '" +
                song.getId() +
                "');";

        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println("Playlist_song inserted");
        } else {
            System.out.println("Failed to insert playlist_song");
        }
    }

    public ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        String qu = "SELECT * FROM " + SONG_TABLE_NAME;

        try {
            ResultSet rs = execQuery(qu);
            while (rs.next()) {
                int id = rs.getInt("id");
                String file = rs.getString("file");
                String title = rs.getString("title");
                String author = rs.getString("author");

                String addedStr = rs.getString("added");
                Date added = Date.valueOf(addedStr);

                int color = rs.getInt("color");
                songs.add(new Song(id, file, title, author, added, color));
            }
            System.out.println("Songs got successfully");
            return songs;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get songs");
            return null;
        }
    }

    private Song getSong(int id) {
        ArrayList<Song> songs = getSongs();
        for (Song s : songs) {
            if (s.getId() == id) {
                return s;
            }
        }
        System.out.println("Couldn't find song");
        return null;
    }

    public void changeSongColor(Song songToChange, int color) {
        String qu = "UPDATE " + SONG_TABLE_NAME +
                " SET color = '" + color + "' WHERE id = " + songToChange.getId();
        System.out.println(qu);
        if (execAction(qu)) {
            System.out.println("Song color changed");
        } else {
            System.out.println("Song color failed to change");
        }
    }

    public void deleteSong(Song songToDelete) {
        String qu = "DELETE FROM " + SONG_TABLE_NAME + " WHERE id = " + songToDelete.getId();
        System.out.println(qu);
        if (execAction(qu)) {
            System.out.println("Song deleted");
        } else {
            System.out.println("Song failed to delete");
        }

        String qu2 = "DELETE FROM " + PLAYLIST_SONG_TABLE_NAME + " WHERE song_id = " + songToDelete.getId();
        System.out.println(qu2);
        if (execAction(qu2)) {
            System.out.println("Song deleted in playlist_songs");
        } else {
            System.out.println("Song failed to delete in playlist_songs");
        }
    }

    private ArrayList<Playlist> getPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        String qu = "SELECT * FROM " + PLAYLIST_TABLE_NAME;

        try {
            ResultSet rs = execQuery(qu);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String iconFile = rs.getString("icon_file");

                String addedStr = rs.getString("added");
                Date added = Date.valueOf(addedStr);

                playlists.add(new Playlist(id, name, iconFile, added));
            }
            System.out.println("Playlists got successfully");
            return playlists;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get playlists");
            return null;
        }
    }

    public void changePlaylistName(Playlist playlistToChange, String newPlaylistName) {
        String qu = "UPDATE " + PLAYLIST_TABLE_NAME +
                " SET name = '" + newPlaylistName + "' WHERE id = " + playlistToChange.getId();
        System.out.println(qu);
        if (execAction(qu)) {
            System.out.println("Playlist name changed");
        } else {
            System.out.println("Playlist name failed to change");
        }
    }

    public void deletePlaylist(Playlist playlistToDelete) {
        String qu = "DELETE FROM " + PLAYLIST_TABLE_NAME + " WHERE id = " + playlistToDelete.getId();
        System.out.println(qu);
        if (execAction(qu)) {
            System.out.println("Playlist deleted");
        } else {
            System.out.println("Playlist failed to delete");
        }

        String qu2 = "DELETE FROM " + PLAYLIST_SONG_TABLE_NAME + " WHERE playlist_id = " + playlistToDelete.getId();
        System.out.println(qu2);
        if (execAction(qu2)) {
            System.out.println("Playlist deleted in playlist_songs");
        } else {
            System.out.println("Playlist failed to delete in playlist_songs");
        }
    }

    public ArrayList<Playlist> getPlaylistsWSongs() {
        ArrayList<Playlist> playlists = getPlaylists();

        try {
            for (Playlist p : playlists) {
                ArrayList<Song> songs = new ArrayList<>();

                String qu = "SELECT * FROM " + PLAYLIST_SONG_TABLE_NAME +
                        " WHERE playlist_id = '" + p.getId() + "';";
                ResultSet rs = execQuery(qu); //contains playlist & song id's
                while (rs.next()) {
                    int songId = rs.getInt("song_id");
                    songs.add(getSong(songId));
                }
                p.setSongs(songs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get playlistsWSongs");
            return null;
        }

        return playlists;
    }

    public void deletePlaylistSong(Playlist playlist, Song song) {
        String qu = "DELETE FROM " + PLAYLIST_SONG_TABLE_NAME + " WHERE playlist_id = " + playlist.getId() + " AND song_id = " + song.getId();

        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(song.getTitle() + " deleted from " + playlist.getName());
        } else {
            System.out.println("Failed to delete " + song.getTitle() + " from " + playlist.getName());
        }
    }

    public void dropAll() {
        String qu = "DROP TABLE " + SONG_TABLE_NAME;
        System.out.println(qu);
        execAction(qu);

        qu = "DROP TABLE " + PLAYLIST_TABLE_NAME;
        System.out.println(qu);
        execAction(qu);

       qu = "DROP TABLE " + PLAYLIST_SONG_TABLE_NAME;
        System.out.println(qu);
        execAction(qu);
    }

    /**
     * Executes the command given by the query, altering the database
     * @param qu query as an SQL formated string
     * @return true if query was successfully executed, false otherwise
     */
    private boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("execAction failed, did not alter database");
        }
        return false;
    }

    /**
     * Executes the command given by the query, returning a resultSet from the database as instructed by the query
     * @param qu query as an SQL formated string
     * @return ResultSet as asked for by the query
     */
    private ResultSet execQuery(String qu){
        ResultSet resultSet;
        try{
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(qu);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve result set");
            return null;
        }
        return resultSet;
    }
}
