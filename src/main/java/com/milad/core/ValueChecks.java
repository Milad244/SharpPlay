package com.milad.core;

/**
 * Static class to ensure user input follows my requirements.
 */
public class ValueChecks {
    public static final int textMin = 1;
    public static final int textMax = 30;
    public static final String minErrorText = "Text must be at least " + textMin + " character long";
    public static final String maxErrorText = "Text can't be over " + textMax + " characters long";

    /**
     * Checks if the text is greater than or equal to the minimum allowed text length.
     * @param text the text to be checked
     * @return returns true if text is allowed, returns false if text is not allowed
     */
    public static boolean minTextCheck(String text) {
        return text.length() >= textMin;
    }

    /**
     * Checks if the text is less than or equal to the maximum allowed text length.
     * @param text the text to be checked
     * @return returns true if text is allowed, returns false if text is not allowed
     */
    public static boolean maxTextCheck(String text) {
        return text.length() <= textMax;
    }
}
