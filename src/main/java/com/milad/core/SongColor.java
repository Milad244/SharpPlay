package com.milad.core;

import javafx.scene.paint.Color;

/**
 * An enum that represents a song color and also holds its javafx color equivalent.
 */
public enum SongColor {
    BLACK("Black", Color.BLACK),
    GREEN("Green", Color.GREEN),
    BLUE("Blue", Color.BLUE),
    PURPLE("Purple", Color.PURPLE),
    RED("Red", Color.RED);

    private final String colorName;
    private final Color fxColor;

    /**
     * Constructor for SongColor.
     * @param colorName the name of the color, as a String
     * @param fxColor the javafx paint color of the color, as a Color type
     */
    SongColor(String colorName, Color fxColor) {
        this.colorName = colorName;
        this.fxColor = fxColor;
    }

    public String getColorName() {
        return colorName;
    }

    public Color getFxColor() {
        return fxColor;
    }

    /**
     * Gets the color from its ordinal number
     * @param ordinal the ordinal of a given color, as an int
     * @return the color from its ordinal number, as a SongColor enum
     */
    public static SongColor fromOrdinal(int ordinal) {
        return values()[ordinal];
    }
}
