package com.milad.gui;

import com.milad.core.IconHandler;
import com.milad.core.Playlist;
import com.milad.database.DatabaseHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class NewPlaylistController implements Initializable{

    public TextField playlistNameField;
    public ImageView fromFileImageView;

    private DatabaseHandler db;
    private String newPlaylistIconFile;
    private static final String PLAYLIST_DEFAULT_ICON = "src/main/resources/icons/Default_Playlist_Icon.png";
    private static final String PLAYLIST_ICONS_DIR = "User_Data/Playlist_Icons";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseHandler.getHandler();
    }

    public void selectDefaultIcon() {
        newPlaylistIconFile = PLAYLIST_DEFAULT_ICON;
    }

    private Stage getStage() {
        return (Stage) playlistNameField.getScene().getWindow();
    }

    public void selectFileIcon() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(getStage());
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
        String playlistName = playlistNameField.getText(); //add checks to this later
        System.out.println(playlistName);

        // Copying icon to local directory if it is not the default icon or already there
        if (!newPlaylistIconFile.equals(PLAYLIST_DEFAULT_ICON)) {
            File source = new File(newPlaylistIconFile).getAbsoluteFile();
            File destination = new File(PLAYLIST_ICONS_DIR, source.getName()).getAbsoluteFile();
            if (!source.equals(destination)) {
                try {
                    FileUtils.copyFile(source, destination);
                    newPlaylistIconFile = destination.getPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        db.insertPlaylist(new Playlist(playlistName, newPlaylistIconFile));
        newPlaylistIconFile = null;
        getStage().close();
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
