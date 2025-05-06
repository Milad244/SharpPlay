package com.milad.gui;

import com.milad.core.MusicPlayer;
import com.milad.core.Playlist;
import com.milad.core.Song;
import com.milad.core.SongColor;
import com.milad.database.DatabaseHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    public ListView<Playlist> playlistList;
    public ListView<Playlist> playlistAddRList;
    public ListView<Song> songsList;
    public VBox homeVBox;
    public VBox libraryVBox;
    public VBox songsVBox;
    public HBox playlistControlsHBox;
    public VBox manageVBox;
    public ListView<Playlist> managePlaylistsList;
    public ListView<Song> manageSongsList;
    public ImageView playStateImageView;
    public ImageView playModeImageView;

    private DatabaseHandler db;
    private static final String NEW_PLAYLIST_FXML_PATH = "/fxml/newPlaylistWindow.fxml";
    private static final String NEW_SONG_FXML_PATH = "/fxml/newSongWindow.fxml";

    private static final String PLUS_ICON_PATH = "src/main/resources/icons/Plus_Icon.png";
    private static final String MINUS_ICON_PATH = "src/main/resources/icons/Minus_Icon.png";

    private static final String PAUSE_ICON_PATH = "src/main/resources/icons/Pause_Icon.png";
    private static final String PLAY_ICON_PATH = "src/main/resources/icons/Play_Icon.png";
    private static final String LOOP_ICON_PATH = "src/main/resources/icons/Repeat_Icon.png";
    private static final String ONE_LOOP_ICON_PATH = "src/main/resources/icons/Repeat_One_Icon.png";
    private static final String SHUFFLE_ICON_PATH = "src/main/resources/icons/Shuffle_Icon.png";

    private enum mainMode {
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

    private ChangeListener<Playlist> mainListener = null;
    private ChangeListener<Playlist> addRListener = null;

    private MusicPlayer mp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        db = DatabaseHandler.getHandler();

        System.out.println(db.getPlaylistsWSongs());
        System.out.println(db.getSongs());

        mainMode.HOME.setContainer(homeVBox);
        mainMode.LIBRARY.setContainer(libraryVBox);
        mainMode.SONG.setContainer(songsVBox);

        loadPlaylistList();
        changeMode(mainMode.HOME);

        initiateMusicPlayer();
    }

    private void changeMode(mainMode mainMode) {
        for (mainMode m : mainMode.values()) {
            GUIHelper.showRegion(m.getContainer(), false);
        }

        // Clears playlist selection if no longer in playlist
        if (mainMode != mainMode.SONG) {
            playlistList.getSelectionModel().clearSelection();
        }

        GUIHelper.showRegion(mainMode.getContainer(), true);
    }

    public void loadHome() {
        changeMode(mainMode.HOME);
    }

    public void loadLibrary() {
        changeMode(mainMode.LIBRARY);
        GUIHelper.showRegion(manageSongsList, false);
        GUIHelper.showRegion(managePlaylistsList, false);
    }

    private void initiateMusicPlayer() {
        mp = new MusicPlayer();
        mp.startWithDefaultSettings();
    }

    public void togglePlayState() {
        mp.togglePausedState();
    }

    public void reloadPlayState() {
        if (mp.disabled()) {
            GUIHelper.disableImageView(playStateImageView, true);
            return;
        } else {
            GUIHelper.disableImageView(playStateImageView, false);
        }

        if (mp.isPaused()) {
            playStateImageView.setImage(GUIHelper.getImage(PLAY_ICON_PATH));
        } else {
            playStateImageView.setImage(GUIHelper.getImage(PAUSE_ICON_PATH));
        }
    }

    public void togglePlayMode() {
        mp.togglePlayMode();
    }

    public void reloadPlayMode() {
        switch (mp.getPlayMode()) {
            case ONE_LOOP -> playModeImageView.setImage(GUIHelper.getImage(ONE_LOOP_ICON_PATH));
            case LOOP -> playModeImageView.setImage(GUIHelper.getImage(LOOP_ICON_PATH));
            case SHUFFLE -> playModeImageView.setImage(GUIHelper.getImage(SHUFFLE_ICON_PATH));
        }
    }

    public void playNext() {
        mp.playNextSong();
    }

    public void playPrev() {
        mp.playPrevSong();
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
                loadAddRPlaylistList(s);
            }
        });
        Button finishedBtn = new Button();
        finishedBtn.setText("Finished Adding/Removing Songs From Playlists");
        finishedBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadPlaylistList();
            }
        });
        HBox addRHBox = new HBox(addRBtn, finishedBtn);
        addRHBox.setAlignment(Pos.CENTER);
        addRHBox.setSpacing(5);

        // Color
        Label colorChangeLbl = new Label();
        colorChangeLbl.setText("Change Song Color");
        HBox changeColorBtnsHBox = new HBox();
        for (SongColor c : SongColor.values()) {
            Button changeColorBtn = new Button();
            changeColorBtn.setText(c.getColorName());
            changeColorBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    db.changeSongColor(s, c.ordinal());
                    loadPlaylistList();
                    loadManageSongs();
                }
            });
            changeColorBtnsHBox.getChildren().add(changeColorBtn);
        }
        changeColorBtnsHBox.setAlignment(Pos.CENTER);
        changeColorBtnsHBox.setSpacing(5);
        VBox colorVBox = new VBox(colorChangeLbl, changeColorBtnsHBox);
        colorVBox.setAlignment(Pos.CENTER);

        // Delete
        Button deleteBtn = new Button();
        deleteBtn.setText("Delete Song");
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                db.deleteSong(s);
                loadPlaylistList();
                loadManageSongs();
            }
        });
        HBox deleteHBox = new HBox(deleteBtn);
        deleteHBox.setAlignment(Pos.CENTER);

        manageVBox.getChildren().addAll(statsVBox, addRHBox, colorVBox, deleteHBox);
    }

    public void loadPlaylistList() {
        GUIHelper.showRegion(playlistAddRList, false);
        GUIHelper.showRegion(playlistList, true);

        playlistList.getItems().clear();

        ArrayList<Playlist> playlists = db.getPlaylistsWSongs();
        playlistList.getItems().addAll(playlists);

        if (mainListener != null) {
            playlistList.getSelectionModel().selectedItemProperty().removeListener(mainListener);
        }

        mainListener = (obs, oldVal, newVal) -> {
            if (newVal != null) {
                changeMode(mainMode.SONG);
                loadPlaylistOptions(newVal);
                loadSongList(newVal);
            }
        };
        playlistList.getSelectionModel().selectedItemProperty().addListener(mainListener);

        GUIHelper.updatePlaylistListDisplay(playlistList);
    }

    private Boolean songExists(Playlist playlist, Song song) {
        for (Song s : playlist.getSongs()) {
            if (s.getId() == song.getId()) {
                return true;
            }
        }
        return false;
    }

    private void loadAddRPlaylistList(Song addRSong) {
        GUIHelper.showRegion(playlistList, false);
        GUIHelper.showRegion(playlistAddRList, true);

        playlistAddRList.getItems().clear();

        ArrayList<Playlist> playlists = db.getPlaylistsWSongs();
        playlistAddRList.getItems().addAll(playlists);

        if (addRListener != null) {
            playlistAddRList.getSelectionModel().selectedItemProperty().removeListener(addRListener);
        }

        addRListener = (obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (songExists(newVal, addRSong)) {
                    db.deletePlaylistSong(newVal, addRSong);
                } else {
                    db.insertPlaylistSong(newVal, addRSong);
                }
                Platform.runLater(() -> { // run later ensures we are not changing UI when user is interacting with it
                    loadAddRPlaylistList(addRSong); //refresh options after action
                });
            }
        };
        playlistAddRList.getSelectionModel().selectedItemProperty().addListener(addRListener);

        playlistAddRList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    if (songExists(item, addRSong)) {
                        setGraphic(GUIHelper.getIcon(MINUS_ICON_PATH));
                    } else {
                        setGraphic(GUIHelper.getIcon(PLUS_ICON_PATH));
                    }
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
                mp.playNewSong(selectedPlaylist, newVal);
            }
        });

        GUIHelper.updateSongsListDisplay(songsList);
    }

    public void openNewPlaylistWindow() {
        if (newPlaylistStage == null || !newPlaylistStage.isShowing()) {
            newPlaylistStage = new Stage();
            GUIHelper.openNewWindow(NEW_PLAYLIST_FXML_PATH, newPlaylistStage, "Create New Playlist", 640, 480);
        } else {
            newPlaylistStage.toFront();
        }
    }

    public void openNewSongWindow() {
        if (newSongStage == null || !newSongStage.isShowing()) {
            newSongStage = new Stage();
            GUIHelper.openNewWindow(NEW_SONG_FXML_PATH, newSongStage, "Create New Song", 640, 480);
        } else {
            newSongStage.toFront();
        }
    }
}
