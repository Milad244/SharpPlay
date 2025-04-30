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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    public ListView<Playlist> menuList;
    public ListView<Song> songsList;
    public VBox homeVBox;
    public VBox libraryVBox;
    public VBox songsVBox;
    public HBox playlistControlsHBox;

    private DatabaseHandler db;
    private enum Mode {
        HOME, LIBRARY, SONG;
        private Region container;

        public void setContainer(Region container) {
            this.container = container;
        }

        public Region getContainer() {
            return container;
        }
    }
    private Stage newPlaylistStage;
    private Stage newSongStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseHandler.getHandler();

        System.out.println(db.getPlaylistsWSongs());
        System.out.println(db.getSongs());

        Mode.HOME.setContainer(homeVBox);
        Mode.LIBRARY.setContainer(libraryVBox);
        Mode.SONG.setContainer(songsVBox);

        loadMenuList();
        changeMode(Mode.HOME);
    }

    private void showRegion(Region region, Boolean show) {
        region.setVisible(show);
        region.setManaged(show);
    }

    private void changeMode(Mode mode) {
        for (Mode m : Mode.values()) {
            showRegion(m.getContainer(), false);
        }

        // Clears playlist selection if no longer in playlist
        if (mode != Mode.SONG) {
            menuList.getSelectionModel().clearSelection();
        }

        showRegion(mode.getContainer(), true);
    }

    public void loadHome() {
        changeMode(Mode.HOME);
    }

    public void loadLibrary() {
        changeMode(Mode.LIBRARY);
    }

    private void loadMenuList() {
        menuList.getItems().clear();

        ArrayList<Playlist> playlists = db.getPlaylistsWSongs();
        menuList.getItems().addAll(playlists);

        menuList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                changeMode(Mode.SONG);
                loadPlaylistOptions(newVal);
                loadSongList(newVal);
            }
        });

        menuList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    setGraphic(GUIHelper.getIcon(item.getIconFile()));
                }
            }
        });
    }

    private void loadPlaylistOptions(Playlist selectedPlaylist) {
        playlistControlsHBox.getChildren().clear();

        // Add stuff like start playlist here (playlist controls)
    }

    private void loadSongList(Playlist selectedPlaylist) {
        songsList.getItems().clear();

        songsList.getItems().addAll(selectedPlaylist.getSongs());

        songsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                playSong(newVal);
            }
        });

        songsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                    // Set song color here
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
                newPlaylistStage.setMinWidth(640);
                newPlaylistStage.setMinHeight(480);
                newPlaylistStage.show();
                newPlaylistStage.setWidth(640);
                newPlaylistStage.setHeight(480);
            } else {
                newPlaylistStage.toFront();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load window");
        }
    }

    public void openNewSongWindow() {
        try {
            if (newSongStage == null || !newSongStage.isShowing()) {
                URL fxmlUrl = getClass().getResource("/fxml/newSongWindow.fxml");
                if (fxmlUrl == null) {
                    throw new RuntimeException("FXML file not found");
                }
                Parent parent = FXMLLoader.load(fxmlUrl);
                newSongStage = new Stage();
                newSongStage.setTitle("Create New Song");
                newSongStage.setScene(new Scene(parent));
                newSongStage.setMinWidth(640);
                newSongStage.setMinHeight(480);
                newSongStage.show();
                newSongStage.setWidth(640);
                newSongStage.setHeight(480);
            } else {
                newSongStage.toFront();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load window");
        }
    }
}
