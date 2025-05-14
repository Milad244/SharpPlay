package com.milad.gui;

import com.milad.core.Playlist;
import com.milad.core.Song;
import com.milad.core.SongColor;
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

import java.io.*;
import java.net.URL;
import java.util.stream.Stream;

/**
 * Static class to help with reused GUI actions.
 */
public class GUIHelper {
    /**
     * Gets an icon from its file path, puts it in an imageView and returns it.
     * @param filepath the file path of the icon, as a String
     * @return the imageView with the icon, as an imageView
     */
    public static ImageView getIcon(String filepath) {
        // First checking if from dir
        File file = new File(filepath);
        if (file.exists()) {
            try {
                InputStream stream = new FileInputStream(filepath);
                Image iconImage = new Image(stream);
                ImageView imageView = new ImageView();
                imageView.setImage(iconImage);

                //Styling
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);

                return imageView;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        // If here, then it's in resources

        //Getting image and putting it into imageview
        InputStream iconStream = GUIHelper.class.getResourceAsStream(filepath);
        if (iconStream == null) {
            throw new RuntimeException("File not found");
        }
        Image iconImage = new Image(iconStream);
        ImageView imageView = new ImageView();
        imageView.setImage(iconImage);

        //Styling
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    /**
     * Gets an image stream using its file path and returns an Image with that stream.
     * @param filepath the file path of the image, as a String
     * @return the image with the image stream, as an Image
     */
    public static Image getImage(String filepath) {
        InputStream imageStream = GUIHelper.class.getResourceAsStream(filepath);
        if (imageStream == null) {
            throw new RuntimeException("File not found");
        }
        return new Image(imageStream);
    }

    /**
     * Enables or disables an imageView both functionally and visibly.
     * @param imageView the imageView to modify, as an imageView
     * @param disable true to disable (and dim) the image, false to enable (and restore the opacity).
     */
    public static void disableImageView (ImageView imageView, boolean disable) {
        imageView.setDisable(disable);
        if (disable) {
            imageView.setOpacity(0.5);
        } else {
            imageView.setOpacity(1);
        }
    }

    /**
     * Creates an error alert with a given error message.
     * @param error the error message, as a String
     */
    public static void giveUserError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("User Error");
        alert.setContentText(error);
        alert.showAndWait();
    }

    /**
     * Gets the stage that a region is from.
     * @param region the region we want the stage from
     * @return the stage from the given region
     */
    public static Stage getStage(Region region) {
        return (Stage) region.getScene().getWindow();
    }

    /**
     * Updates a listview of songs visually. For a non-null song, it renames it to the song's title and sets its given color.
     * @param songListView the listview of songs to update visually, as a ListView of Song type
     */
    public static void updateSongsListDisplay(ListView<Song> songListView) {
        songListView.getStyleClass().add("song-listView");
        songListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                    setTextFill(SongColor.fromOrdinal(item.getColor()).getFxColor());
                }
            }
        });
    }

     /**
     * Updates a listview of playlists visually. For a non-null playlist, it renames it to the playlist's name and sets its given icon.
     * @param playlistListView the listview of playlists to update visually, as a ListView of Playlist type
     */
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
                    setGraphic(getIcon(item.getIconFile()));
                }
            }
        });
    }

    /**
     * Opens a new window.
     * @param fxmlPath the fxml file path of the new window, as a String
     * @param newStage the stage of the new window, as a Stage
     * @param windowTitle the title of the new window, as a String
     * @param width the width of the new window, as a double
     * @param height the height of the new window, as a double
     */
    public static void openNewWindow(String fxmlPath, Stage newStage, String windowTitle, double width, double height) {
        URL fxmlUrl = GUIHelper.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new RuntimeException("FXML file not found");
        }
        URL cssUrl = GUIHelper.class.getResource("/css/style.css");
        if (cssUrl == null) {
            throw new RuntimeException("CSS file not found");
        }
        try {
            Parent parent = FXMLLoader.load(fxmlUrl);
            newStage.setTitle(windowTitle);
            newStage.setScene(new Scene(parent));
            newStage.getScene().getStylesheets().add(cssUrl.toExternalForm());
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

    /**
     * Shows or doesn't show a region.
     * @param region the region to show or not show
     * @param show true to show, false to not show
     */
    public static void showRegion(Region region, boolean show) {
        region.setVisible(show);
        region.setManaged(show);
    }
}
