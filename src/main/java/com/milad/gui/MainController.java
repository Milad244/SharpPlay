package com.milad.gui;

import com.milad.core.IconHandler;
import com.milad.core.Playlist;
import com.milad.core.Song;
import com.milad.database.DatabaseHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseHandler.getHandler();
        Mode.HOME.setContainer(homeVBox);
        Mode.LIBRARY.setContainer(libraryVBox);
        Mode.SONG.setContainer(songsList);

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
                    setGraphic(IconHandler.getIcon(item.getIconFile()));
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
        songsList.setCellFactory(param -> new ListCell<>() {
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
