package com.milad.gui;

import com.milad.core.Playlist;
import com.milad.database.DatabaseHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class NewPlaylistController implements Initializable{

    public TextField playlistNameField;
    public ImageView defaultImageView;
    public ImageView fromFileImageView;
    public Button selectDefaultBtn;
    public Button selectFromFileBtn;

    private DatabaseHandler db;
    private String newPlaylistIconFile;
    private static final String PLAYLIST_DEFAULT_ICON = "src/main/resources/icons/Default_Playlist_Icon.png";
    private static final String PLAYLIST_ICONS_DIR = "User_Data/Playlist_Icons";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseHandler.getHandler();
        selectDefaultIcon();
    }

    private void selectedFromFile(Boolean fromFile) {
        if (!fromFile) {
            selectFromFileBtn.setText("Select");
            selectDefaultBtn.setText("Selected");
        } else {
            selectFromFileBtn.setText("Selected");
            selectDefaultBtn.setText("Select");
        }
    }

    public void selectDefaultIcon() {
        GUIHelper.displayIcon(defaultImageView, PLAYLIST_DEFAULT_ICON);
        newPlaylistIconFile = PLAYLIST_DEFAULT_ICON;
        selectedFromFile(false);
    }

    public void selectFileIcon() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(GUIHelper.getStage(playlistNameField));
        if (file == null) {
            return;
        }
        String path = file.getPath();

        try {
            // checking if the user file is an image
            InputStream stream = new FileInputStream(path);
            if (ImageIO.read(stream) == null) {
                GUIHelper.giveUserError("Selected file is not a supported image file");
                return;
            }

            GUIHelper.displayIcon(fromFileImageView, path);
            newPlaylistIconFile = path;
            selectedFromFile(true);
        } catch (IOException e) {
            GUIHelper.giveUserError("Could not read icon file");
        }
    }

    public void createPlaylist() {
        String playlistName = playlistNameField.getText(); //add checks to this later

        if (!newPlaylistIconFile.equals(PLAYLIST_DEFAULT_ICON)) { //Skipping if default icon
            File source = new File(newPlaylistIconFile);
            File destination = new File(PLAYLIST_ICONS_DIR, source.getName());
            if (!destination.exists()) { //If file doesn't already exist in icon directory
                try {
                    FileUtils.copyFile(source, destination);
                } catch (IOException e) {
                    GUIHelper.giveUserError("Could not copy icon file");
                    return;
                }
            }
            newPlaylistIconFile = destination.getPath();
        }

        db.insertPlaylist(new Playlist(playlistName, newPlaylistIconFile, new Date(System.currentTimeMillis())));
        MainController.getInstance().loadPlaylistList();

        GUIHelper.getStage(playlistNameField).close();
    }
}
