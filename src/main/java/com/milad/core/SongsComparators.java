package com.milad.core;


import java.util.Comparator;

public class SongsComparators {
    /**
     * Gets a Song comparator for a given order.
     * @param type what to sort the songs by, as a SongSortType enum
     * @param ascending if in ascending order, as a boolean
     * @return a song comparator for a given order, as a Comparator
     */
    public static Comparator<Song> getComparator(SongSortType type, boolean ascending) {
        Comparator<Song> comparator;
        switch(type) {
            case DATE -> comparator = Comparator.comparing(Song::getAdded);
            case COLOR -> comparator = Comparator.comparing(Song::getColor);
            case PLAYS -> comparator = Comparator.comparing(Song::getPlayCount).reversed();
            default -> throw new IllegalArgumentException("Unknown sort type");
        }
        return ascending ? comparator : comparator.reversed(); // If not ascending, then reverse the comparator order
    }
}
