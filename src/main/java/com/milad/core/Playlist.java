package com.milad.core;

import java.sql.Date;
import java.util.ArrayList;

public class Playlist {
    private final int id;
    private final String name;
    private final String iconFile;
    private final Date added;
    private ArrayList<Song> songs;

    // Make methods with this later
    private static final int nameMin = 1;
    private static final int nameMax = 20;

    public Playlist(String name, String iconFile, Date added) {
        id = -1;
        this.name = name;
        this.iconFile = iconFile;
        this.added = added;
        songs = null;
    }

    public Playlist(int id, String name, String iconFile, Date added) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
        this.added = added;
        this.songs = null;
    }

    public Playlist(int id, String name, String iconFile, Date added, ArrayList<Song> songs) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
        this.added = added;
        this.songs = songs;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconFile() {
        return iconFile;
    }

    public Date getAdded() {
        return added;
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
                ", iconFile='" + iconFile + '\'' +
                ", added=" + added +
                ", songs=" + songs +
                '}';
    }
}
