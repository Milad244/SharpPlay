package com.milad.core;

public class ValueChecks {
    public static final int textMin = 1;
    public static final int textMax = 20;
    public static final String minErrorText = "Text must be at least " + textMin + " character long";
    public static final String maxErrorText = "Text can't be over " + textMax + " characters long";

    public static boolean minTextCheck(String text) {
        return text.length() >= textMin;
    }

    public static boolean maxTextCheck(String text) {
        return text.length() <= textMax;
    }
}
