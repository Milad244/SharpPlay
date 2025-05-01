package com.milad.gui;

import com.milad.core.Playlist;
import com.milad.core.Song;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

    public static void updateSongsListDisplay(ListView<Song> songListView) {
        songListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                    // Set song color here
                }
            }
        });
    }

    public static void updatePlaylistListDisplay(ListView<Playlist> playlistListView) {
        playlistListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    setGraphic(GUIHelper.getIcon(item.getIconFile()));
                }
            }
        });
    }

    public static void openNewWindow(String fxmlPath, Stage newStage, String windowTitle, double width, double height) {
        URL fxmlUrl = GUIHelper.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new RuntimeException("FXML file not found");
        }
        try {
            Parent parent = FXMLLoader.load(fxmlUrl);
            newStage.setTitle(windowTitle);
            newStage.setScene(new Scene(parent));
            newStage.setMinWidth(width);
            newStage.setMinHeight(height);
            newStage.show();
            newStage.setWidth(width);
            newStage.setHeight(height);
        }
        catch (IOException e) {
            System.out.println("Could not load window");
        }
    }

    public static void showRegion(Region region, Boolean show) {
        region.setVisible(show);
        region.setManaged(show);
    }
}
