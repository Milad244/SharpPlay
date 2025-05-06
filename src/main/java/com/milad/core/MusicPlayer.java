package com.milad.core;

import com.milad.gui.MainController;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MusicPlayer {
    private final MainController mainController = MainController.getInstance();

    private Playlist currentPlaylist;
    private Song currentSong;
    private ArrayList<Song> playingOrder;
    private boolean paused;
    private double volume;
    private PlayMode playMode;

    private MediaPlayer mediaPlayer;

    public MusicPlayer() {
        playingOrder = new ArrayList<>();
    }

    public void startWithDefaultSettings() {
        setVolume(0.3);
        setPausedState(true);
        setPlayMode(PlayMode.ONE_LOOP);
    }

    public boolean disabled() {
        return mediaPlayer == null;
    }

    public void playNewSong(Playlist playlist, Song song) {
        currentPlaylist = playlist;
        setPlayingOrder(playlist, song);
        playSong(song);
    }

    private void setPlayingOrder(Playlist playlist, Song song) {
        switch (playMode) {
            case ONE_LOOP:
                playingOrder.clear();
                playingOrder.add(song);
                break;
            case LOOP:
                playingOrder = playlist.getSongs();
                break;
            case SHUFFLE:
                playingOrder = playlist.getSongs();
                Collections.shuffle(playingOrder);
        }
    }

    public void playNextSong() {
        if (disabled()) {
            return;
        }

        int currentIndex = playingOrder.indexOf(currentSong);
        Song nextSong;

        if (playingOrder.size() > currentIndex + 1) {
            nextSong = playingOrder.get(currentIndex + 1);
        } else {
            nextSong = playingOrder.getFirst();
        }
        playSong(nextSong);
    }

    public void playPrevSong() {
        if (disabled()) {
            return;
        }

        int currentIndex = playingOrder.indexOf(currentSong);
        Song prevSong;

        if (currentIndex - 1 >= 0) {
            prevSong = playingOrder.get(currentIndex - 1);
        } else {
            prevSong = playingOrder.getLast();
        }
        playSong(prevSong);
    }

    private void playSong(Song song) {
        currentSong = song;
        File songFile = new File(currentSong.getFile());
        Media songMedia = new Media(songFile.toURI().toString());

        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        mediaPlayer = new MediaPlayer(songMedia);
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                playNextSong();
            }
        });

        setPausedState(false);
        setVolume(volume);
    }

    private void setPausedState(boolean paused) {
        this.paused = paused;
        pauseAction();
    }

    public void togglePausedState() {
        paused = !paused;

        pauseAction();
    }

    private void pauseAction() {
        mainController.reloadPlayState();

        if (disabled()) {
            return;
        }

        if (paused) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setVolume(double volume) {
        this.volume = volume;

        if (disabled()) {
            return;
        }

        mediaPlayer.setVolume(volume);
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    private void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
        playModeAction();
    }

    public void togglePlayMode (){
        playMode = playMode.toggle();
        playModeAction();
    }

    private void playModeAction() {
        mainController.reloadPlayMode();

        if (disabled()) {
            return;
        }
        setPlayingOrder(currentPlaylist, currentSong);
    }
}
