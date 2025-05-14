package com.milad.gui;

import com.milad.core.*;
import com.milad.database.DatabaseHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.time.Year;
import java.util.*;

/**
 * The controller for the main window. This controls the entire app aside from the creation of new playlists and songs.
 */
public class MainController implements Initializable {

    public ListView<Playlist> playlistList;
    public ListView<Playlist> playlistAddRList;
    public ListView<Song> songsList;
    public VBox homeVBox;
    public VBox libraryVBox;
    public VBox statsVBox;
    public VBox songsVBox;
    public VBox manageVBox;
    public ListView<Playlist> managePlaylistsList;
    public ListView<Song> manageSongsList;
    public ImageView playStateImageView;
    public ImageView playModeImageView;
    public Label songNameLbl;
    public Label songAuthorLbl;
    public Slider volumeSlider;
    public Slider timelineSlider;
    public Label currentTimeLbl;
    public Label maxTimeLbl;
    public HBox sortSongsHBox;
    public Label modeLbl;
    public HBox modeLblHBox;

    private DatabaseHandler db;
    private static final String NEW_PLAYLIST_FXML_PATH = "/fxml/newPlaylistWindow.fxml";
    private static final String NEW_SONG_FXML_PATH = "/fxml/newSongWindow.fxml";

    private static final String PLUS_ICON_PATH = "/icons/Plus_Icon.png";
    private static final String MINUS_ICON_PATH = "/icons/Minus_Icon.png";

    private static final String PAUSE_ICON_PATH = "/icons/Pause_Icon.png";
    private static final String PLAY_ICON_PATH = "/icons/Play_Icon.png";
    private static final String LOOP_ICON_PATH = "/icons/Repeat_Icon.png";
    private static final String ONE_LOOP_ICON_PATH = "/icons/Repeat_One_Icon.png";
    private static final String SHUFFLE_ICON_PATH = "/icons/Shuffle_Icon.png";

    /**
     * An enum that represents what mode of our app we are in.
     */
    private enum MainMode {
        HOME("Home"), LIBRARY("Library"), STATS("Statistics"), SONG("");
        private final String name;
        private  Region container;

        MainMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setContainer(Region container) {
            this.container = container;
        }

        public Region getContainer() {
            return container;
        }
    }
    
    private Stage newPlaylistStage;
    private Stage newSongStage;

    // Allowing access to my reload gui methods
    private static MainController instance;
    public static MainController getInstance() {
        return instance;
    }

    private ChangeListener<Playlist> mainListener = null;
    private ChangeListener<Playlist> addRListener = null;
    private ChangeListener<Song> playSongListener = null;
    private ChangeListener<Playlist> managePListener = null;
    private ChangeListener<Song> manageSListener = null;

    private MusicPlayer mp;
    private boolean isUserChangingTimeline = false;
    private int statYear = Year.now().getValue();
    private BarChart<String, Number> playBarChart;

    /**
     * Sets its instances, get the DB, adds all the containers to the MainMode enum and starts on the mode Home.
     * Also, loads the playlists and initiates the music playback controls.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        db = DatabaseHandler.getHandler();

        MainMode.HOME.setContainer(homeVBox);
        MainMode.LIBRARY.setContainer(libraryVBox);
        MainMode.STATS.setContainer(statsVBox);
        MainMode.SONG.setContainer(songsVBox);

        loadPlaylistList();
        changeMode(MainMode.HOME);

        initiateMusicControls();
    }

    /**
     * Changes the apps main mode, disabling all the other mode containers and updating the mode title.
     * If switching to a mode other than songs, clears the song sorting buttons and playlist selection.
     * @param mainMode
     */
    private void changeMode(MainMode mainMode) {
        for (MainMode m : MainMode.values()) {
            GUIHelper.showRegion(m.getContainer(), false);
        }

        if (mainMode != MainMode.SONG) {
            playlistList.getSelectionModel().clearSelection();
            GUIHelper.showRegion(modeLblHBox, true);
            GUIHelper.showRegion(sortSongsHBox, false);
        } else {
            GUIHelper.showRegion(modeLblHBox, false);
            GUIHelper.showRegion(sortSongsHBox, true);
        }

        GUIHelper.showRegion(mainMode.getContainer(), true);
        modeLbl.setText(mainMode.getName());
    }

    /**
     * Changes the main mode to home. Home is where we add new songs/playlists and where the instructions for the program are.
     */
    public void loadHome() {
        changeMode(MainMode.HOME);
    }

    /**
     * Changes the main mode to library. Library is where we manage songs/playlists.
     */
    public void loadLibrary() {
        changeMode(MainMode.LIBRARY);
        GUIHelper.showRegion(manageSongsList, false);
        GUIHelper.showRegion(managePlaylistsList, false);
        manageVBox.getChildren().clear();
    }

    /**
     * Changes the main mode to stats and loads the play count bar chart.
     */
    public void loadStats() {
        changeMode(MainMode.STATS);
        loadPlayBarChart();
    }

    /**
     * Changes the play count year for the barchart to the previous.
     */
    public void setPrevStatYear() {
        statYear --;
        loadPlayBarChart();
    }

    /**
     * Changes the play count year for the barchart to the next.
     */
    public void setNextStatYear() {
        statYear ++;
        loadPlayBarChart();
    }

    /**
     * Loads the play count bar chart for the year of the statYear field. First, we create a hashmap of months for the year.
     * Then, we take all the songs and their plays. Next, we iterate through them, adding their plays to their respective months.
     * Finally, we create and update the bar chart.
     */
    private void loadPlayBarChart() {
        if (playBarChart != null) statsVBox.getChildren().remove(playBarChart); // To ensure we do not create duplicates

        LinkedHashMap<Months, Integer> monthsPlaysMap= new LinkedHashMap<>();

        for (Months month : Months.values()) {
            monthsPlaysMap.put(month, 0);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        ArrayList<Song> songs = db.getSongsWPlays();

        for (Song s : songs) {
            for (Date date : s.getPlays()) {
                int year = date.getYear() + 1900; // getYear() subtracts 1900 for some reason - so I add it back
                if (year != statYear) continue;

                Months month = Months.getMonthFromInt(date.getMonth());
                monthsPlaysMap.put(month, monthsPlaysMap.get(month) + 1);
            }
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Plays");

        playBarChart = new BarChart<>(xAxis, yAxis);
        playBarChart.setTitle(String.valueOf(statYear));
        playBarChart.setLegendVisible(false);

        // Reference: https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
        for (Map.Entry<Months, Integer> entry : monthsPlaysMap.entrySet()) {
            Months x = entry.getKey();
            Number y = entry.getValue();
            series.getData().add(new XYChart.Data<>(x.getName(), y));
        }

        playBarChart.getData().add(series);

        statsVBox.getChildren().add(playBarChart);
    }

    /**
     * Initiates our Music Player with its default settings and music controls like volume and timeline.
     */
    private void initiateMusicControls() {
        mp = new MusicPlayer();
        mp.startWithDefaultSettings();
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            mp.setVolume(newVal.doubleValue());
        });
        volumeSlider.setValue(0.2);

        timelineSlider.setOnMousePressed(event -> isUserChangingTimeline = true);
        timelineSlider.setOnMouseReleased(event -> {
            isUserChangingTimeline = false;
            mp.setTimeline(timelineSlider.getValue());
        });

        timelineSlider.setDisable(true);
    }

    /**
     * Reloads the timeline for the next song with the new song's end time.
     */
    public void reloadTimeline() {
        timelineSlider.setDisable(false);
        timelineSlider.setMax(mp.getEndTime());
        maxTimeLbl.setText(MusicPlayer.formatTime(mp.getEndTime()));
    }

    /**
     * Updates the timeline and current time label. Called from the music player each second of the song.
     */
    public void updateTimeline() {
        timelineSlider.setValue(mp.getTimeline());

        Platform.runLater(() -> {
            currentTimeLbl.setText(MusicPlayer.formatTime(mp.getTimeline()));
        });
    }

    /**
     * Toggles between pausing and un-pausing the song in the music player.
     */
    public void togglePlayState() {
        mp.togglePausedState();
    }

    /**
     * Reloads the play state icons based on the music player's playing state.
     */
    public void reloadPlayState() {
        if (mp.disabled()) {
            GUIHelper.disableImageView(playStateImageView, true);
            songNameLbl.setText("");
            songAuthorLbl.setText("");
            return;
        } else {
            GUIHelper.disableImageView(playStateImageView, false);
            songNameLbl.setText(mp.getCurrentSong().getTitle());
            songAuthorLbl.setText(mp.getCurrentSong().getAuthor());
        }

        if (mp.isPaused()) {
            playStateImageView.setImage(GUIHelper.getImage(PLAY_ICON_PATH));
        } else {
            playStateImageView.setImage(GUIHelper.getImage(PAUSE_ICON_PATH));
        }
    }

    /**
     * Toggles the play mode of the song in the music player.
     */
    public void togglePlayMode() {
        mp.togglePlayMode();
    }

    /**
     * Reloads the play mode icons based on the music player's play mode state.
     */
    public void reloadPlayMode() {
        switch (mp.getPlayMode()) {
            case ONE_LOOP -> playModeImageView.setImage(GUIHelper.getImage(ONE_LOOP_ICON_PATH));
            case LOOP -> playModeImageView.setImage(GUIHelper.getImage(LOOP_ICON_PATH));
            case SHUFFLE -> playModeImageView.setImage(GUIHelper.getImage(SHUFFLE_ICON_PATH));
        }
    }

    /**
     * Plays the next song in the music player.
     */
    public void playNext() {
        mp.playNextSong();
    }

    /**
     * plays the previous song in the music player.
     */
    public void playPrev() {
        mp.playPrevSong();
    }

    /**
     * Opens the Google Drive folder with the demo songs and demo playlist icons in the user's browser.
     */
    public void openDemoLink() {
        // Reference: https://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
        try {
            Desktop.getDesktop().browse(new URL("https://drive.google.com/drive/folders/1JKmSNRG-hiY1XNmhGUu3FPC8WuW50zOR?usp=sharing").toURI());
        } catch (Exception e) {
            System.out.println("Could not open link");
        }
    }

    /**
     * Loads the managing of playlists.
     */
    public void loadManagePlaylists() {
        GUIHelper.showRegion(manageSongsList, false);
        GUIHelper.showRegion(managePlaylistsList, true);

        managePlaylistsList.getItems().clear();
        manageVBox.getChildren().clear();

        ArrayList<Playlist> playlists = db.getPlaylistsWSongs();
        managePlaylistsList.getItems().addAll(playlists);

        // I do this throughout my loading of GUI to ensure I do not create multiple listeners because even after an item is clear, it's listener still exists.
        if (managePListener != null) {
            managePlaylistsList.getSelectionModel().selectedItemProperty().removeListener(managePListener);
        }

        managePListener = (obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadManage(newVal);
            }
        };

        managePlaylistsList.getSelectionModel().selectedItemProperty().addListener(managePListener);

        GUIHelper.updatePlaylistListDisplay(managePlaylistsList);
    }

    /**
     * Loads the managing of songs.
     */
    public void loadManageSongs() {
        GUIHelper.showRegion(manageSongsList, true);
        GUIHelper.showRegion(managePlaylistsList, false);

        manageSongsList.getItems().clear();
        manageVBox.getChildren().clear();

        ArrayList<Song> songs = db.getSongsWPlays();
        manageSongsList.getItems().addAll(songs);

        if (manageSListener != null) {
            manageSongsList.getSelectionModel().selectedItemProperty().removeListener(manageSListener);
        }

        manageSListener = (obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadManage(newVal);
            }
        };

        manageSongsList.getSelectionModel().selectedItemProperty().addListener(manageSListener);

        GUIHelper.updateSongsListDisplay(manageSongsList);
    }

    /**
     * Loads the managing options for a given playlist. The user can see the information of the playlist, rename it, and delete it.
     * @param p the playlist to be managed, as a Playlist type
     */
    private void loadManage(Playlist p) {
        manageVBox.getChildren().clear();

        // Stats
        Label nameLbl = new Label();
        nameLbl.setText(p.getName());
        Label dateLbl = new Label();
        dateLbl.setText("Date added: " + p.getAdded());
        Label fileLbl = new Label();
        fileLbl.setText("Icon file path: " + p.getIconFile());
        VBox statsVBox = new VBox(nameLbl, dateLbl, fileLbl);
        statsVBox.setAlignment(Pos.CENTER);

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

        manageVBox.getChildren().addAll(statsVBox, renameHBox, deleteHBox);
    }

    /**
     * Loads the managing options for a given song. The user can see the information of the song,
     * add/remove it from various playlists, change its color, and delete it.
     * @param s the song to be managed, as a Song type
     */
    private void loadManage(Song s) {
        manageVBox.getChildren().clear();

        // Stats
        Label titleLbl = new Label();
        titleLbl.setText(s.getTitle());
        Label authorLbl = new Label();
        authorLbl.setText("Author: " + s.getAuthor());
        Label dateLbl = new Label();
        dateLbl.setText("Date added: " + s.getAdded());
        Label playLbl = new Label();
        playLbl.setText("Play count: " + s.getPlayCount());
        Label fileLbl = new Label();
        fileLbl.setText("Song file path: " + s.getFile());
        VBox statsVBox = new VBox(titleLbl, authorLbl, dateLbl, playLbl, fileLbl);
        statsVBox.setAlignment(Pos.CENTER);

        // Add/Remove
        Button addRBtn = new Button();
        addRBtn.setText("Add/Remove From Playlists");
        addRBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadAddRPlaylistList(s);
            }
        });
        HBox addRHBox = new HBox(addRBtn);
        addRHBox.setAlignment(Pos.CENTER);
        addRHBox.setSpacing(5);

        // Color
        Label colorChangeLbl = new Label();
        colorChangeLbl.setText("Change Song Color");
        HBox changeColorBtnsHBox = new HBox();
        for (SongColor c : SongColor.values()) {
            Button changeColorBtn = new Button();
            changeColorBtn.setText(c.getColorName());
            changeColorBtn.setTextFill(c.getFxColor());
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

    /**
     * Loads the all the playlists into the playlist listview, and adds the listener to load its songs as well.
     */
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
                changeMode(MainMode.SONG);
                loadSongList(newVal);
            }
        };
        playlistList.getSelectionModel().selectedItemProperty().addListener(mainListener);

        GUIHelper.updatePlaylistListDisplay(playlistList);
    }

    /**
     * Checks if a given song exists in a given playlist.
     * @param playlist the playlist to check if the song exists in it, as a Playlist type
     * @param song the song to check if it's in the playlist, as a Song type
     * @return true if the song exists in the playlist, false if it does not exist in the playlist
     */
    private Boolean songExists(Playlist playlist, Song song) {
        for (Song s : playlist.getSongs()) {
            if (s.getId() == song.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loads a plus icon on playlist's that can be added to, loads a minus icon on playlist's that can be removed from.
     * Also, adds the listeners and logic for the adding/removing to work.
     * @param addRSong the song to be added/removed from various playlists, as a Song type
     */
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
                loadPlaylistList();
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

    /**
     * Loads the songs from a user selected playlist and adds listeners and logic to play the song.
     * Also, adds the sorting buttons to sort the songs in the playlist.
     * @param selectedPlaylist the playlist the user selected, as a Playlist type
     */
    private void loadSongList(Playlist selectedPlaylist) {
        songsList.getItems().clear();

        songsList.getItems().addAll(selectedPlaylist.getSongs());

        if (playSongListener != null) {
            songsList.getSelectionModel().selectedItemProperty().removeListener(playSongListener);
        }

        playSongListener = (obs, oldVal, newVal) -> {
            if (newVal != null) {
                mp.playNewSong(selectedPlaylist, newVal);
                Platform.runLater(() -> songsList.getSelectionModel().clearSelection());
            }
        };

        songsList.getSelectionModel().selectedItemProperty().addListener(playSongListener);

        GUIHelper.updateSongsListDisplay(songsList);

        sortSongsHBox.getChildren().clear();
        for (SongSortType songSortType : SongSortType.values()) {
            Button sortBtn = new Button();
            sortBtn.setFont(new Font(14));
            sortBtn.setText("Sort by " + songSortType.getSortBtnText());
            sortBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    // Sorting toggles between ascending and descending order for a given playlist
                    selectedPlaylist.orderSongs(songSortType, selectedPlaylist.getSortSongsAscending());
                    loadSongList(selectedPlaylist);
                }
            });
            sortSongsHBox.getChildren().add(sortBtn);
        }
    }

    /**
     * Opens the new playlist window if it isn't already open, otherwise, it brings the existing window to the front.
     */
    public void openNewPlaylistWindow() {
        if (newPlaylistStage == null || !newPlaylistStage.isShowing()) {
            newPlaylistStage = new Stage();
            GUIHelper.openNewWindow(NEW_PLAYLIST_FXML_PATH, newPlaylistStage, "Create New Playlist", 640, 480);
        } else {
            newPlaylistStage.toFront();
        }
    }

    /**
     * Opens the new song window if it isn't already open, otherwise, it brings the existing window to the front.
     */
    public void openNewSongWindow() {
        if (newSongStage == null || !newSongStage.isShowing()) {
            newSongStage = new Stage();
            GUIHelper.openNewWindow(NEW_SONG_FXML_PATH, newSongStage, "Create New Song", 640, 480);
        } else {
            newSongStage.toFront();
        }
    }
}
