package com.milad.gui;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GUIHelper {
    public static void displayIcon(ImageView imageView, String filepath) {
        try {
            InputStream stream = new FileInputStream(filepath);
            Image icon = new Image(stream);
            imageView.setImage(icon);

            //Styling
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
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

    public static void giveUserError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("User Error");
        alert.setContentText(error);
        alert.showAndWait();
    }

    public static Stage getStage(Region region) {
        return (Stage) region.getScene().getWindow();
    }
}
