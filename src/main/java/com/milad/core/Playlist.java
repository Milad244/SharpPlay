package com.milad.core;

import java.util.ArrayList;

public class Playlist {
    private final int id;
    private final String name;
    private final String iconFile;
    private ArrayList<Song> songs;

    public Playlist(String name, String iconFile) {
        id = -1;
        this.name = name;
        this.iconFile = iconFile;
        songs = null;
    }

    public Playlist(int id, String name, String iconFile) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
        this.songs = null;
    }

    public Playlist(int id, String name, String iconFile, ArrayList<Song> songs) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
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
                ", songs=" + songs +
                '}';
    }
}
