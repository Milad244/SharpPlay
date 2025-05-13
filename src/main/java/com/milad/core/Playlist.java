package com.milad.core;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Represents a Playlist.
 */
public class Playlist {
    private final int id;
    private final String name;
    private final String iconFile;
    private final Date added;
    private ArrayList<Song> songs;

    private boolean sortSongsAscending = true;

    /**
     * Constructor for Playlist without its id or songs.
     * @param name the name of the playlist, as a String
     * @param iconFile the file path of the playlist's icon, as a String
     * @param added the date this playlist was added, as a Date
     */
    public Playlist(String name, String iconFile, Date added) {
        id = -1;
        this.name = name;
        this.iconFile = iconFile;
        this.added = added;
        songs = null;
    }

    /**
     * Constructor for Playlist without its songs.
     * @param id the id of the playlist, as an int
     * @param name the name of the playlist, as a String
     * @param iconFile the file path of the playlist's icon, as a String
     * @param added the date this playlist was added, as a Date
     */
    public Playlist(int id, String name, String iconFile, Date added) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
        this.added = added;
        this.songs = null;
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

    /**
     * Orders the songs and updates sortSongsAscending field to be the opposite of the given.
     * @param songSortType what to sort the songs by, as a SongSortType enum
     * @param ascending if in ascending order, as a boolean
     */
    public void orderSongs(SongSortType songSortType, boolean ascending) {
        songs.sort(SongsComparators.getComparator(songSortType, ascending));
        sortSongsAscending = !sortSongsAscending;
    }

    public boolean getSortSongsAscending() {
        return sortSongsAscending;
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
