package com.milad.core;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class IconHandler {
    public static void displayIcon(ImageView imageView, String filepath) {
        try {
            InputStream stream = new FileInputStream(filepath);
            Image icon = new Image(stream);
            imageView.setImage(icon);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static ImageView getIcon(String filepath) {
        try {
            //Getting image and putting it into imageview
            InputStream stream = new FileInputStream(filepath);
            Image icon = new Image(stream);
            ImageView imageView = new ImageView();
            imageView.setImage(icon);

            //Styling
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);

            return imageView;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
