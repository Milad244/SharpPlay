package com.milad.gui;

import com.milad.core.Playlist;
import com.milad.core.Song;
import com.milad.database.DatabaseHandler;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class Main {
    private static DatabaseHandler db;

    public static void main(String[] args) {
        MainJFXLauncher.main(args);

        /*
        // For testing

        db = DatabaseHandler.getHandler();
        // The following methods are commands. Please only do one each runtime

        //db.dropAll();
        test();
        printValues();
        */
    }

    private static void test() {
        // Adding songs
        Time duration = new Time(0, 3, 51);
        Time t1 = new Time(0, 2, 35);
        Time t2 = new Time(0, 1, 21);
        Time t3 = new Time(0, 0, 2);
        Date added = new Date(System.currentTimeMillis());

        db.insertSong(new Song("Burn.mp3", "Burn.png", "Burn", "Ellie Goulding", duration, added, 1));
        db.insertSong(new Song("T1.mp3", "T11.png", "T11", "M A", t1, added, 4));
        db.insertSong(new Song("T2.mp3", "T22.png","T22", "Mil Ab", t2, added, 0));
        db.insertSong(new Song("T3.mp3", "T33.png","T33", "Milad Abdi", t3, added, 3));

        // Adding playlists
        db.insertPlaylist(new Playlist("P1", "P1.png"));
        db.insertPlaylist(new Playlist("P2", "P2.png"));

        // Adding songs to playlists
        ArrayList<Playlist> playlists = db.getPlaylistsWSongs();
        ArrayList<Song> songs = db.getSongs();
        db.insertPlaylistSong(playlists.get(1), songs.getFirst()); // P2-Burn
        db.insertPlaylistSong(playlists.get(0), songs.getFirst()); // P1-Burn
        db.insertPlaylistSong(playlists.get(0), songs.get(2)); // P1-T22
    }

    private static void printValues() {
        ArrayList<Song> songs = db.getSongs();
        System.out.println(songs);
        ArrayList<Playlist> playlists = db.getPlaylistsWSongs();
        System.out.println(playlists);
    }
}
