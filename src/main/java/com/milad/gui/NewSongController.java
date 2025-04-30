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

        // Copying icon to local directory if it is not already there
        File source = new File(newSongFile).getAbsoluteFile();
        File destination = new File(SONG_FILE_DIR, source.getName()).getAbsoluteFile();
        if (!source.equals(destination)) {
            try {
                FileUtils.copyFile(source, destination);
                newSongFile = destination.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            newSongFile = destination.getPath();
        }

        db.insertSong(new Song(newSongFile, songName, songAuthor, new Date(System.currentTimeMillis()), 0));
        GUIHelper.getStage(songNameField).close();
    }
}
