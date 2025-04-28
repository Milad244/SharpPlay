package com.milad.gui;

import com.milad.core.IconHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.*;

public class NewPlaylistController {

    public TextField playlistNameField;
    public ImageView fromFileImageView;
    private String newPlaylistIconFile;
    private static final String PLAYLIST_DEFAULT_ICON = "src/main/resources/icons/Default_Playlist_Icon.png";
    private static final String PLAYLIST_ICONS_DIR = "User_Data/Playlist_Icons";

    public void selectDefaultIcon() {
        newPlaylistIconFile = PLAYLIST_DEFAULT_ICON;
    }

    public void selectFileIcon() {
        Stage stage;
        stage = (Stage) playlistNameField.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        String path = file.getPath();

        try {
            // checking if the user file is an image
            InputStream stream = new FileInputStream(path);
            if (ImageIO.read(stream) == null) {
                giveUserError("Uploaded file is not an image");
                return;
            }

            IconHandler.displayIcon(fromFileImageView, path);
            newPlaylistIconFile = path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void createPlaylist() {
        String playlistName = playlistNameField.getText();
        System.out.println(playlistName);

        // Copying icon to local directory if not default
        if (!newPlaylistIconFile.equals(PLAYLIST_DEFAULT_ICON)) {
            File source = new File(newPlaylistIconFile);
            File destination = new File(PLAYLIST_ICONS_DIR, source.getName());
            try {
                FileUtils.copyFile(source, destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Alerts user of their error
     * @param error why what the user is trying to do is not allowed as a string
     */
    private void giveUserError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("User Error");
        alert.setContentText(error);
        alert.showAndWait();
    }
}
