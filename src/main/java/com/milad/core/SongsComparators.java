package com.milad.core;


import java.util.Comparator;

public class SongsComparators {
    public static Comparator<Song> getComparator(SongSortType type, boolean ascending) {
        Comparator<Song> comparator;
        switch(type) {
            case DATE -> comparator = Comparator.comparing(Song::getAdded);
            case COLOR -> comparator = Comparator.comparing(Song::getColor);
            case PLAYS -> comparator = Comparator.comparing(Song::getPlayCount).reversed();
            default -> throw new IllegalArgumentException("Unknown sort type");
        }
        return ascending ? comparator : comparator.reversed();
    }
}
