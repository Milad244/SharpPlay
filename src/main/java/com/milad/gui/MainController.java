package com.milad.gui;

import com.milad.core.Playlist;
import com.milad.core.Song;
import com.milad.database.DatabaseHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public ListView<Playlist> menuList;
    public ListView<Song> songsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMenuList();
    }

    private void loadMenuList() {
        menuList.getItems().clear();

        ArrayList<Playlist> playlists = DatabaseHandler.getHandler().getPlaylistsWSongs();
        menuList.getItems().addAll(playlists);

        menuList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
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
                loadSong(newVal);
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

    private void loadSong(Song selectedSong) {
        // TBD
    }
}
