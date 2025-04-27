package com.milad.gui;

import com.milad.core.Playlist;
import com.milad.core.Song;
import com.milad.database.DatabaseHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public ListView<Playlist> menuList;
    public ListView<Song> songsList;
    public VBox homeVBox;

    private enum Mode {
        HOME, SONG
    }
    private Stage newPlaylistStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMenuList();

        changeMode(Mode.HOME);
    }

    private void changeMode(Mode mode) {
        songsList.setVisible(false);
        songsList.setManaged(false);
        homeVBox.setVisible(false);
        homeVBox.setManaged(false);
        if (mode.equals(Mode.SONG)) {
            songsList.setVisible(true);
            songsList.setManaged(true);
        } else if (mode.equals(Mode.HOME)) {
            homeVBox.setVisible(true);
            homeVBox.setManaged(true);
        }
    }

    public void loadHome() {
        changeMode(Mode.HOME);
    }

    private void loadMenuList() {
        menuList.getItems().clear();

        ArrayList<Playlist> playlists = DatabaseHandler.getHandler().getPlaylistsWSongs();
        menuList.getItems().addAll(playlists);

        menuList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                changeMode(Mode.SONG);
                loadSongList(newVal);
            }
        });

        // Will add icon here too
        menuList.setCellFactory(playlistListView -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    private void loadSongList(Playlist selectedPlaylist) {
        songsList.getItems().clear();

        songsList.getItems().addAll(selectedPlaylist.getSongs());

        songsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                playSong(newVal);
            }
        });

        // Will add icon here too
        songsList.setCellFactory(songListView -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });
    }

    private void playSong(Song selectedSong) {
        // TBD
    }

    public void openNewPlaylistWindow() {
        try {
            if (newPlaylistStage == null || !newPlaylistStage.isShowing()) {
                URL fxmlUrl = getClass().getResource("/fxml/newPlaylistWindow.fxml");
                if (fxmlUrl == null) {
                    throw new RuntimeException("FXML file not found");
                }
                Parent parent = FXMLLoader.load(fxmlUrl);
                newPlaylistStage = new Stage();
                newPlaylistStage.setTitle("Create New Playlist");
                newPlaylistStage.setScene(new Scene(parent));
                newPlaylistStage.setMinWidth(800);
                newPlaylistStage.setMinHeight(600);
                newPlaylistStage.show();
                newPlaylistStage.setWidth(800);
                newPlaylistStage.setHeight(600);
            } else {
                newPlaylistStage.toFront();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load window");
        }
    }
}
