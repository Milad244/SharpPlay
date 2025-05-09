package com.milad.core;

import java.sql.Date;
import java.util.ArrayList;

public class Song {
    private final int id;
    private final String file;
    private final String title;
    private final String author;
    private final Date added;
    private final int color;
    private ArrayList<Date> plays;

    public Song(String file, String title, String author, Date added, int color) {
        id = -1;
        this.file = file;
        this.title = title;
        this.author = author;
        this.added = added;
        this.color = color;
        this.plays = null;
    }

    public Song(int id, String file, String title, String author, Date added, int color) {
        this.id = id;
        this.file = file;
        this.title = title;
        this.author = author;
        this.added = added;
        this.color = color;
        this.plays = null;
    }

    public int getId() {
        return id;
    }

    public String getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Date getAdded() {
        return added;
    }

    public int getColor() {
        return color;
    }

    public ArrayList<Date> getPlays() {
        return plays;
    }

    public void setPlays(ArrayList<Date> plays) {
        this.plays = plays;
    }

    public int getPlayCount() {
        return plays.size();
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", file='" + file + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", added=" + added +
                ", color=" + color +
                ", plays=" + plays +
                '}';
    }
}
