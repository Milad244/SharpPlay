package com.milad.core;

import java.sql.Date;
import java.sql.Time;

public class Song {
    private final int id;
    private final String file;
    private final String iconFile;
    private final String title;
    private final String author;
    private final Time duration;
    private final Date added;
    private final int color;

    public Song(String file, String iconFile, String title, String author, Time duration, Date added, int color) {
        id = -1;
        this.file = file;
        this.iconFile = iconFile;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.added = added;
        this.color = color;
    }

    public Song(int id, String file, String iconFile, String title, String author, Time duration, Date added, int color) {
        this.id = id;
        this.file = file;
        this.iconFile = iconFile;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.added = added;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getFile() {
        return file;
    }

    public String getIconFile() {
        return iconFile;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Time getDuration() {
        return duration;
    }

    public Date getAdded() {
        return added;
    }

    public int getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", file='" + file + '\'' +
                ", iconFile='" + iconFile + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", duration=" + duration +
                ", added=" + added +
                ", color=" + color +
                '}';
    }
}
