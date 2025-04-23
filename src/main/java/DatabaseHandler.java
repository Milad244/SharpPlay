import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {
    private static final String DB_url = "jdbc:sqlite:sharpPlay_db";
    private static Connection conn = null;
    private static Statement stmt = null;
    public static DatabaseHandler handler;

    private static final String SONG_TABLE_NAME = "SONG_TABLE";
    private static final String PLAYLIST_TABLE_NAME = "PLAYLIST_TABLE";

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
        // Initiate tables
        initSongTable();
        initPlaylistTable();
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
                + "	file_name VARCHAR(255),"
                + "	title VARCHAR(255),"
                + "	author VARCHAR(255),"
                + "	duration TIME,"
                + " added DATE,"
                + " color INTEGER"
                + ");";
        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println("Songs table made/exists");
        } else {
            System.out.println("Songs table query failed");
        }
    }

    public void insertSong(Song song) {
        String qu = "INSERT INTO " + SONG_TABLE_NAME + " (id, file_name, title, author, duration, added, color) VALUES (" +
                song.getId() + ", '" +
                song.getFileName() + "', '" +
                song.getTitle() + "', '" +
                song.getAuthor() + "', '" +
                song.getDuration() + "', '" +
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

    public ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        String qu = "SELECT * FROM " + SONG_TABLE_NAME;

        try {
            ResultSet rs = execQuery(qu);
            while (rs.next()) {
                int id = rs.getInt("id");
                String file_name = rs.getString("file_name");
                String title = rs.getString("title");
                String author = rs.getString("author");

                String durationStr = rs.getString("duration");
                Time duration = Time.valueOf(durationStr);

                String addedStr = rs.getString("added");
                Date added = Date.valueOf(addedStr);

                int color = rs.getInt("color");
                songs.add(new Song(id, file_name, title, author, duration, added, color));
            }
            System.out.println("Songs got successfully");
            return songs;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get songs");
            return null;
        }
    }

    private void initPlaylistTable() {

    }

    public void dropAll() {
        String qu = "DROP TABLE " + SONG_TABLE_NAME;
        System.out.println(qu);

        if (execAction(qu)) {
            System.out.println("Songs table dropped");
        } else {
            System.out.println("Songs table failed to drop");
        }
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
