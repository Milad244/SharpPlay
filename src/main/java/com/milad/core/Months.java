package com.milad.core;

/**
 * An enum to keep track of the months of the year. Used for my play count bar graph.
 */
public enum Months {
    JANUARY("January"),
    FEBRUARY("February"),
    MARCH("March"),
    APRIL("April"),
    MAY("May"),
    JUNE("June"),
    JULY("July"),
    AUGUST("August"),
    SEPTEMBER("September"),
    OCTOBER("October"),
    NOVEMBER("November"),
    DECEMBER("December");

    private final String name;

    /**
     * Constructor for a month.
     * @param name the name of the month, as a string
     */
    Months(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets a month corresponding with a given index from 0-11.
     * @param i the index of a month, with January being 0 and December being 11
     * @return the month corresponding to its index, as a Months enum
     */
    public static Months getMonthFromInt(int i) {
        return Months.values()[i];
    }
}

