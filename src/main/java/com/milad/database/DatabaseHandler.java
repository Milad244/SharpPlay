package com.milad.database;
import com.milad.core.Song;
import com.milad.core.Playlist;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

/**
 * Local database for persistence and managing songs and playlists.
 */
public class DatabaseHandler {
    private static final String DB_url = "jdbc:sqlite:User_Data/SharpPlay_DB";
    private static Connection conn = null;
    private static Statement stmt = null;
    public static DatabaseHandler handler;

    private static final String SONG_TABLE_NAME = "SONG_TABLE";
    private static final String PLAYLIST_TABLE_NAME = "PLAYLIST_TABLE";
    private static final String PLAYLIST_SONG_TABLE_NAME = "PLAYLIST_SONG_TABLE";
    private static final String SONG_PLAYS_TABLE_NAME = "SONG_PLAYS_TABLE";

    /**
     * Returns database handler which is used to run my database's methods.
     * @return returns a new handler if a database handler has not been initiated, else returns the one already initiated
     */
    public static DatabaseHandler getHandler(){
        if(handler == null){
            handler = new DatabaseHandler();
        }
        return handler;
    }

    /**
     * Runs methods that initiate the database.
     */
    private DatabaseHandler() {
        connect();
        initSongTable();
        initPlaylistTable();
        initPlaylistSongTable();
        initSongPlaysTable();
    }

    /**
     * Creates User_Data directory if it doesn't exist and sets connection to the database.
     */
    private void connect() {
        try {
            // Reference: https://stackoverflow.com/questions/3634853/how-to-create-a-directory-in-java
            // Creating the User_Data directory if it doesn't exist
            new File("User_Data").mkdirs();
            conn = DriverManager.getConnection(DB_url);
            System.out.println("Connection Successful");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection Failed");
        }
    }

    /**
     * Creates a song table if the table does not already exist.
     * This table is used to keep track of songs.
     */
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

    /**
     * Creates a playlist table if the table does not already exist.
     * This table is used to keep track of playlists.
     */
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

    /**
     * Creates a playlist_song table if the table does not already exist.
     * This table is used to connect a song with a playlist.
     */
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

    /**
     * Creates a song_plays table if the table does not already exist.
     * This table is used to connect a play (of a song) with a song.
     */
    private void initSongPlaysTable() {
        String qu = "CREATE TABLE IF NOT EXISTS " + SONG_PLAYS_TABLE_NAME + " ("
                + "	song_id INTEGER,"
                + "	date_played DATE"
                + ");";
        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(SONG_PLAYS_TABLE_NAME + " made/exists");
        } else {
            System.out.println(SONG_PLAYS_TABLE_NAME + " query failed");
        }
    }

    /**
     * Inserts a new song to the song table.
     * @param song the new song to be inserted, as a Song type
     */
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
            System.out.println(SONG_TABLE_NAME + " inserted");
        } else {
            System.out.println(SONG_TABLE_NAME + " failed to insert");
        }
    }

    /**
     * Inserts a new playlist to the playlist table.
     * @param playlist the new playlist to be inserted, as a Playlist type
     */
    public void insertPlaylist(Playlist playlist) {
        String qu = "INSERT INTO " + PLAYLIST_TABLE_NAME + " (name, icon_file, added) VALUES ('" +
                playlist.getName() + "', '" +
                playlist.getIconFile() + "', '" +
                playlist.getAdded() +
                "');";

        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(PLAYLIST_TABLE_NAME + " inserted");
        } else {
            System.out.println(PLAYLIST_TABLE_NAME + " failed to insert");
        }
    }

    /**
     * Inserts a new connection between a song and playlist. In other words, adds a song to a playlist.
     * @param playlist the playlist to add a song to, as a Playlist type
     * @param song the song to add to the playlist, as a Song type
     */
    public void insertPlaylistSong(Playlist playlist, Song song) {
        String qu = "INSERT INTO " + PLAYLIST_SONG_TABLE_NAME + " (playlist_id, song_id) VALUES ('" +
                playlist.getId() + "', '" +
                song.getId() +
                "');";

        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(PLAYLIST_SONG_TABLE_NAME + " inserted");
        } else {
            System.out.println(PLAYLIST_SONG_TABLE_NAME + " failed to insert");
        }
    }

    /**
     * Inserts a new connection between a play and a song. In other words, adds a play count for a given song.
     * @param song the song to add a play count to, as a Song type
     * @param datePlayed the date of the play, as a Date
     */
    public void insertSongPlayed(Song song, Date datePlayed) {
        String qu = "INSERT INTO " + SONG_PLAYS_TABLE_NAME + " (song_id, date_played) VALUES ('" +
                song.getId() + "', '" +
                datePlayed +
                "');";

        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(SONG_PLAYS_TABLE_NAME + " inserted");
        } else {
            System.out.println(SONG_PLAYS_TABLE_NAME + " failed to insert");
        }
    }

    /**
     * Gets all the songs from the song table.
     * @return all the songs from the song table, as an Arraylist of Song type
     */
    private ArrayList<Song> getSongs() {
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

    /**
     * Gets all the songs from the song table and then adds all the plays of each song using the song_plays table.
     * @return all the songs with their plays, as an Arraylist of Song type
     */
    public ArrayList<Song> getSongsWPlays() {
        ArrayList<Song> songs = getSongs();

        try {
            for (Song s : songs) {
                ArrayList<Date> dates = new ArrayList<>();

                String qu = "SELECT * FROM " + SONG_PLAYS_TABLE_NAME + " WHERE song_id = '" + s.getId() + "';";
                ResultSet rs = execQuery(qu);
                while (rs.next()) {
                    String datePlayedStr = rs.getString("date_played");
                    Date datePlayed = Date.valueOf(datePlayedStr);
                    dates.add(datePlayed);
                }
                s.setPlays(dates);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get songsWPlays");
            return null;
        }

        System.out.println("Got songsWPlays");
        return songs;
    }

    /**
     * Gets a song from the song table using the song id.
     * @param id the id of the song
     * @return the song from the song table with the given id, as a Song type
     */
    private Song getSong(int id) {
        ArrayList<Song> songs = getSongsWPlays();
        for (Song s : songs) {
            if (s.getId() == id) {
                return s;
            }
        }
        System.out.println("Couldn't find song");
        return null;
    }

    /**
     * Changes a given song's color in the song table.
     * @param songToChange the song to change, as a Song type
     * @param color the new color for the song, as an int that represents the Color enum ordinal
     */
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

    /**
     * Deletes a given song from the song table and removes any connections with playlists from the playlist_song table.
     * @param songToDelete the song to delete, as a Song type
     */
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

    /**
     * Gets all the playlists from the playlist table.
     * @return all the playlists from the playlist table, as an arraylist of the Playlist type
     */
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

    /**
     * Changes a playlist's name in the playlist table.
     * @param playlistToChange the playlist to change, as a Playlist type
     * @param newPlaylistName the new name for the playlist, as a String
     */
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

    /**
     * Deletes a given playlist from the playlist table and removes any connections with songs from the playlist_song table.
     * @param playlistToDelete the playlist to delete, as a Playlist type
     */
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

    /**
     * Gets all the playlists from the playlist table and then adds all the songs of each playlist using the playlist_song table.
     * @return all the playlists with their songs, as an Arraylist of Playlist type
     */
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

        System.out.println("Got playlistsWSongs");
        return playlists;
    }

    /**
     * Deletes a connection between a song and playlist. In other words, removes a song from a playlist.
     * @param playlist the playlist to remove a song from, as a Playlist type
     * @param song the song to remove from the playlist, as a Song type
     */
    public void deletePlaylistSong(Playlist playlist, Song song) {
        String qu = "DELETE FROM " + PLAYLIST_SONG_TABLE_NAME + " WHERE playlist_id = " + playlist.getId() + " AND song_id = " + song.getId();

        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println(song.getTitle() + " deleted from " + playlist.getName());
        } else {
            System.out.println("Failed to delete " + song.getTitle() + " from " + playlist.getName());
        }
    }

    /**
     * Drops all the tables, effectively resetting the database. Was only used for testing.
     */
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

        qu = "DROP TABLE " + SONG_PLAYS_TABLE_NAME;
        System.out.println(qu);
        execAction(qu);
    }

    /**
     * Executes the command given by the query, altering the database.
     * @param qu the query as an SQL formated string
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
     * Executes the command given by the query, returning a resultSet from the database as instructed by the query.
     * @param qu the query as an SQL formated string
     * @return the ResultSet as asked for by the query
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
