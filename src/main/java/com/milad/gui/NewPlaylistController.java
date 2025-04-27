package com.milad.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class NewPlaylistController {

    public TextField playlistNameField;
    private String playlistIconFile;

    public void selectDefaultIcon() {
        playlistIconFile = "src/main/resources/icons/Default_Playlist_Icon.png";
    }

    public void selectFileIcon() {
        Stage stage;
        stage=(Stage) playlistNameField.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
    }

    public void createPlaylist() {
        String playlistName = playlistNameField.getText();
        System.out.println(playlistName);
        try
        {
            Image picture = ImageIO.read(new File(playlistIconFile));
        }
        catch (IOException e)
        {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            e.printStackTrace();
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
