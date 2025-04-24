import java.util.ArrayList;

public class Playlist {
    private final int id;
    private final String name;
    private ArrayList<Song> songs;

    public Playlist(String name) {
        id = -1;
        this.name = name;
        songs = null;
    }

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
        this.songs = null;
    }

    public Playlist(int id, String name, ArrayList<Song> songs) {
        this.id = id;
        this.name = name;
        this.songs = songs;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", songs=" + songs +
                '}';
    }
}
