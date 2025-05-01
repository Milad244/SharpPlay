package com.milad.gui;

import com.milad.core.Playlist;
import com.milad.core.Song;
import com.milad.database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    public ListView<Playlist> playlistList;
    public ListView<Song> songsList;
    public VBox homeVBox;
    public VBox libraryVBox;
    public VBox songsVBox;
    public HBox playlistControlsHBox;
    public VBox manageVBox;
    public ListView<Playlist> managePlaylistsList;
    public ListView<Song> manageSongsList;

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

    // Allowing new playlist/song controllers to access my reload gui methods
    private static MainController instance;
    public static MainController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;

        db = DatabaseHandler.getHandler();

        System.out.println(db.getPlaylistsWSongs());
        System.out.println(db.getSongs());

        Mode.HOME.setContainer(homeVBox);
        Mode.LIBRARY.setContainer(libraryVBox);
        Mode.SONG.setContainer(songsVBox);

        loadPlaylistList();
        changeMode(Mode.HOME);
    }

    private void changeMode(Mode mode) {
        for (Mode m : Mode.values()) {
            GUIHelper.showRegion(m.getContainer(), false);
        }

        // Clears playlist selection if no longer in playlist
        if (mode != Mode.SONG) {
            playlistList.getSelectionModel().clearSelection();
        }

        GUIHelper.showRegion(mode.getContainer(), true);
    }

    public void loadHome() {
        changeMode(Mode.HOME);
    }

    public void loadLibrary() {
        changeMode(Mode.LIBRARY);
        GUIHelper.showRegion(manageSongsList, false);
        GUIHelper.showRegion(managePlaylistsList, false);
    }

    public void loadManagePlaylists() {
        GUIHelper.showRegion(manageSongsList, false);
        GUIHelper.showRegion(managePlaylistsList, true);

        managePlaylistsList.getItems().clear();
        manageVBox.getChildren().clear();

        ArrayList<Playlist> playlists = db.getPlaylistsWSongs();
        managePlaylistsList.getItems().addAll(playlists);

        managePlaylistsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadManage(newVal);
            }
        });

        GUIHelper.updatePlaylistListDisplay(managePlaylistsList);
    }

    public void loadManageSongs() {
        GUIHelper.showRegion(manageSongsList, true);
        GUIHelper.showRegion(managePlaylistsList, false);

        manageSongsList.getItems().clear();
        manageVBox.getChildren().clear();

        ArrayList<Song> songs = db.getSongs();
        manageSongsList.getItems().addAll(songs);

        manageSongsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadManage(newVal);
            }
        });

        GUIHelper.updateSongsListDisplay(manageSongsList);
    }

    private void loadManage(Playlist p) {
        manageVBox.getChildren().clear();

        // Rename
        Label renameLbl = new Label();
        renameLbl.setText("Rename Playlist: ");
        TextField renameTextField = new TextField();
        Button renameBtn = new Button();
        renameBtn.setText("Rename");
        renameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String rename = renameTextField.getText();
                renameTextField.clear();
                db.changePlaylistName(p, rename);
                loadPlaylistList();
                loadManagePlaylists();
            }
        });
        HBox renameHBox = new HBox(renameLbl, renameTextField, renameBtn);
        renameHBox.setAlignment(Pos.CENTER);
        renameHBox.setSpacing(5);

        // Stats
        Label dateLbl = new Label();
        dateLbl.setText("Date added: " + p.getAdded());
        HBox statsHBox = new HBox(dateLbl);
        statsHBox.setAlignment(Pos.CENTER);

        // Delete
        Button deleteBtn = new Button();
        deleteBtn.setText("Delete Playlist");
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                db.deletePlaylist(p);
                loadPlaylistList();
                loadManagePlaylists();
            }
        });
        HBox deleteHBox = new HBox(deleteBtn);
        deleteHBox.setAlignment(Pos.CENTER);

        manageVBox.getChildren().addAll(statsHBox, renameHBox, deleteHBox);
    }

    private void loadManage(Song s) {
        manageVBox.getChildren().clear();

        // Stats
        Label authorLbl = new Label();
        authorLbl.setText("Author: " + s.getAuthor());
        Label dateLbl = new Label();
        dateLbl.setText("Date added: " + s.getAdded());
        VBox statsVBox = new VBox(authorLbl, dateLbl);
        statsVBox.setAlignment(Pos.CENTER);

        // Add/Remove
        Button addRBtn = new Button();
        addRBtn.setText("Add/Remove Song From Playlists");
        addRBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Logic TBD
            }
        });
        HBox addRHBox = new HBox(addRBtn);
        addRHBox.setAlignment(Pos.CENTER);

        // Color
        // TBD

        // Delete
        Button deleteBtn = new Button();
        deleteBtn.setText("Delete Song");
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Logic TBD
            }
        });
        HBox deleteHBox = new HBox(deleteBtn);
        deleteHBox.setAlignment(Pos.CENTER);

        manageVBox.getChildren().addAll(statsVBox, addRHBox, deleteHBox);
    }

    public void loadPlaylistList() {
        playlistList.getItems().clear();

        ArrayList<Playlist> playlists = db.getPlaylistsWSongs();
        playlistList.getItems().addAll(playlists);

        playlistList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                changeMode(Mode.SONG);
                loadPlaylistOptions(newVal);
                loadSongList(newVal);
                // WILL ADD SONGS USING THIS PLAYLIST LIST AND FROM MANAGE SONGS (- FOR WHEN IN + FOR WHEN NOT IN)
            }
        });

        GUIHelper.updatePlaylistListDisplay(playlistList);
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

        GUIHelper.updateSongsListDisplay(songsList);
    }

    private void playSong(Song selectedSong) {
        // TBD
    }

    public void openNewPlaylistWindow() {
        if (newPlaylistStage == null || !newPlaylistStage.isShowing()) {
            newPlaylistStage = new Stage();
            GUIHelper.openNewWindow("/fxml/newPlaylistWindow.fxml", newPlaylistStage, "Create New Playlist", 640, 480);
        } else {
            newPlaylistStage.toFront();
        }
    }

    public void openNewSongWindow() {
        if (newSongStage == null || !newSongStage.isShowing()) {
            newSongStage = new Stage();
            GUIHelper.openNewWindow("/fxml/newSongWindow.fxml", newSongStage, "Create New Song", 640, 480);
        } else {
            newSongStage.toFront();
        }
    }
}
