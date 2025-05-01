package com.milad.gui;

import com.milad.core.Song;
import com.milad.database.DatabaseHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class NewSongController implements Initializable{

    public TextField songNameField;
    public TextField songAuthorField;

    private DatabaseHandler db;
    private String newSongFile;
    private static final String SONG_FILE_DIR = "User_Data/Songs";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseHandler.getHandler();
    }

    public void selectSongFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(GUIHelper.getStage(songNameField));
        if (file == null) {
            return;
        }
        String path = file.getPath();

        // checking if the user file is audio
        try {
            new Media(file.toURI().toString());
        } catch (Exception e) {
            GUIHelper.giveUserError("Selected file is not a supported audio file");
            return;
        }

        newSongFile = path;
    }

    public void createSong() {
        //add checks to this later
        String songName = songNameField.getText();
        String songAuthor = songAuthorField.getText();

        // Copying song to local directory if it is not already there
        File source = new File(newSongFile);
        File destination = new File(SONG_FILE_DIR, source.getName());
        if (!destination.exists()) {
            try {
                FileUtils.copyFile(source, destination);
            } catch (IOException e) {
                GUIHelper.giveUserError("Could not copy song file");
                return;
            }
        }
        newSongFile = destination.getPath();

        db.insertSong(new Song(newSongFile, songName, songAuthor, new Date(System.currentTimeMillis()), 0));
        MainController.getInstance().loadPlaylistList();

        GUIHelper.getStage(songNameField).close();
    }
}
