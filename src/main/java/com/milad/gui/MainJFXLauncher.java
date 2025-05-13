package com.milad.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The second/final launcher for my program.
 */
public class MainJFXLauncher extends Application {

    /**
     * Gets run from my main launcher and runs the start method.
     * @param args the args from main, as a String[]
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts my primary stage (GUI) and that will run my MainController which will start my program.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = getClass().getResource("/fxml/mainWindow.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("FXML file not found");
        }
        InputStream icon = getClass().getResourceAsStream("/icons/SharpPlay_Icon.png");
        if (icon == null) {
            throw new RuntimeException("Icon file not found");
        }
        // Reference: https://stackoverflow.com/questions/37499885/javafx-css-class-style
        URL cssUrl = getClass().getResource("/css/style.css");
        if (cssUrl == null) {
            throw new RuntimeException("CSS file not found");
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        primaryStage.getIcons().add(new Image(icon));

        primaryStage.setTitle("SharpPlay");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.getScene().getStylesheets().add(cssUrl.toExternalForm());
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
    }
}
