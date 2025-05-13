package com.milad.gui;

import com.milad.core.Song;
import com.milad.core.SongColor;
import com.milad.core.ValueChecks;
import com.milad.database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

/**
 * The controller for the new song window (the window responsible for letting the user add songs to the program).
 */
public class NewSongController implements Initializable{

    public TextField songNameField;
    public TextField songAuthorField;
    public VBox newColorVBox;
    public Button selectSongBtn;

    private DatabaseHandler db;
    private String newSongFile;
    private int newSongColor;
    private static final String SONG_FILE_DIR = "User_Data/Songs";

    /**
     * Gets the database and sets default configurations for a new song.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseHandler.getHandler();

        newSongColor = 0; //Setting default color
        loadNewSongColor();
    }

    /**
     * Adds and displays all the buttons for the user to choose their new song's color.
     */
    private void loadNewSongColor() {
        Label colorLbl = new Label();
        colorLbl.setText("Song Color");
        colorLbl.setFont(new Font(17.5));
        HBox colorBtnsHBox = new HBox();
        for (SongColor c : SongColor.values()) {
            Button changeColorBtn = new Button();
            changeColorBtn.setText(c.getColorName());
            changeColorBtn.setTextFill(c.getFxColor());
            changeColorBtn.setFont(new Font(16));
            changeColorBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    newSongColor = c.ordinal();
                    colorLbl.setTextFill(c.getFxColor());
                }
            });
            colorBtnsHBox.getChildren().add(changeColorBtn);
        }
        colorBtnsHBox.setAlignment(Pos.CENTER);
        colorBtnsHBox.setSpacing(5);
        colorBtnsHBox.setPadding(new Insets(5));
        newColorVBox.getChildren().addAll(colorLbl, colorBtnsHBox);
    }

    /**
     * Lets the user select a file from their computer, checks if the file is allowed, and inputs a suggested song name based on the file name.
     * If the file is not allowed they will be given an error and allowed to try again.
     */
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
        selectSongBtn.setText("Selected From Files");

        String songNameGuess = file.getName();
        // Reference: https://stackoverflow.com/questions/941272/how-do-i-trim-a-file-extension-from-a-string-in-java
        songNameGuess = FilenameUtils.removeExtension(songNameGuess);
        if (songNameGuess.length() > ValueChecks.textMax) {
            songNameGuess = songNameGuess.substring(0, ValueChecks.textMax);
        }
        songNameField.setText(songNameGuess);
    }

    /**
     * Creates a new song based on the user's chosen configurations. If the user has given inappropriate values,
     * they will be given an error and allowed to modify their new song configuration accordingly.
     * If new song is allowed, it will copy the song file to the user data local directory if it's not already there.
     */
    public void createSong() {
        String songName = songNameField.getText();
        String songAuthor = songAuthorField.getText();
        if (!ValueChecks.minTextCheck(songName) || !ValueChecks.minTextCheck(songAuthor)) {
            GUIHelper.giveUserError(ValueChecks.minErrorText);
            return;
        }
        if (!ValueChecks.maxTextCheck(songName) || !ValueChecks.maxTextCheck(songAuthor) ) {
            GUIHelper.giveUserError(ValueChecks.maxErrorText);
            return;
        }
        if (newSongFile == null) {
            GUIHelper.giveUserError("Did not select song file");
            return;
        }

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

        db.insertSong(new Song(newSongFile, songName, songAuthor, new Date(System.currentTimeMillis()), newSongColor));
        MainController.getInstance().loadPlaylistList();

        GUIHelper.getStage(songNameField).close();
    }
}
