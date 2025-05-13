package com.milad.core;

/**
 * An enum that represents the different types to sort a Song by.
 */
public enum SongSortType {
    DATE("date"), COLOR("color"), PLAYS("plays");

    private final String sortBtnText;

    /**
     * Constructor for SongSortType.
     * @param sortBtnText the button text for a sort type, as a String
     */
    SongSortType(String sortBtnText) {
        this.sortBtnText = sortBtnText;
    }

    public String getSortBtnText() {
        return sortBtnText;
    }
}
