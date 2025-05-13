package com.milad.gui;

import com.milad.core.Playlist;
import com.milad.core.ValueChecks;
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

/**
 * The controller for the new playlist window (the window responsible for letting the user add playlists to the program).
 */
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

    /**
     * Gets the database and sets default configurations for a new playlist.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseHandler.getHandler();
        selectDefaultIcon();
    }

    /**
     * Updates the "select default file" button text accordingly.
     * @param fromFile true if selected, false if not selected
     */
    private void selectedFromFile(Boolean fromFile) {
        if (!fromFile) {
            selectFromFileBtn.setText("Select");
            selectDefaultBtn.setText("Selected");
        } else {
            selectFromFileBtn.setText("Selected");
            selectDefaultBtn.setText("Select");
        }
    }

    /**
     * Selects the default icon for a playlist and updates the new playlist icon file field.
     */
    public void selectDefaultIcon() {
        GUIHelper.displayIcon(defaultImageView, PLAYLIST_DEFAULT_ICON);
        newPlaylistIconFile = PLAYLIST_DEFAULT_ICON;
        selectedFromFile(false);
    }

    /**
     * Lets the user select a file from their computer, checks if the file is allowed. If allowed, updates the new playlist icon file field.
     * If not allowed, they will be given an error and allowed to try again.
     */
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

    /**
     * Creates a new playlist based on the user's chosen configurations. If the user has given inappropriate values,
     * they will be given an error and allowed to modify their new playlist configuration accordingly.
     * If new playlist is allowed, it will copy the playlist icon file to the user data local directory if it's not already there.
     */
    public void createPlaylist() {
        String playlistName = playlistNameField.getText();
        if (!ValueChecks.minTextCheck(playlistName)) {
            GUIHelper.giveUserError(ValueChecks.minErrorText);
            return;
        }
        if (!ValueChecks.maxTextCheck(playlistName)) {
            GUIHelper.giveUserError(ValueChecks.maxErrorText);
            return;
        }

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
