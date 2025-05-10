package com.milad.core;

public enum SongSortType {
    DATE("date"), COLOR("color"), PLAYS("plays");

    private final String sortBtnText;

    SongSortType(String sortBtnText) {
        this.sortBtnText = sortBtnText;
    }

    public String getSortBtnText() {
        return sortBtnText;
    }
}
