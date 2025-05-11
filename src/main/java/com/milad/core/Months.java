package com.milad.core;

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

    Months(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Months getMonthFromInt(int i) {
        return Months.values()[i];
    }
}

