package com.milad.core;

import javafx.scene.paint.Color;

/**
 * Song color
 */
public enum SongColor {
    BLACK("Black", Color.BLACK),
    GREEN("Green", Color.GREEN),
    BLUE("Blue", Color.BLUE),
    PURPLE("Purple", Color.PURPLE),
    RED("Red", Color.RED);

    private final String colorName;
    private final Color fxColor;

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

    public static SongColor fromOrdinal(int ordinal) {
        return values()[ordinal];
    }
}
