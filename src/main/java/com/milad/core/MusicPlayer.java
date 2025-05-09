package com.milad.core;

import com.milad.database.DatabaseHandler;
import com.milad.gui.MainController;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer {
    private final MainController mainController = MainController.getInstance();
    private final DatabaseHandler db;

    private Playlist currentPlaylist;
    private Song currentSong;
    private ArrayList<Song> playingOrder;
    private boolean paused;
    private double volume;

    private double timeline;
    private double endTime;
    private Timer timelineTimer;

    private PlayMode playMode;

    private MediaPlayer mediaPlayer;

    public MusicPlayer() {
        db = DatabaseHandler.getHandler();
        playingOrder = new ArrayList<>();
    }

    public void startWithDefaultSettings() {
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
                playingOrder = new ArrayList<>(playlist.getSongs());
                break;
            case SHUFFLE:
                playingOrder = new ArrayList<>(playlist.getSongs());
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
                db.insertSongPlayed(currentSong, new Date(System.currentTimeMillis()));
                playNextSong();
            }
        });

        setPausedState(false);
        setVolume(volume);
        startTimeline();
    }

    private void startTimeline() {
        setEndTime();

        if (timelineTimer != null) {
            timelineTimer.cancel();
        }

        timelineTimer = new Timer();
        TimerTask timelineUpdate = new TimerTask() {
            @Override
            public void run() {
                try {
                    timeline = mediaPlayer.getCurrentTime().toSeconds();
                    mainController.updateTimeline();
                } catch (Exception e) {
                    timelineTimer.cancel();
                    System.out.println("Timer stopped (probably closed program)");
                }
            }
        };
        timelineTimer.scheduleAtFixedRate(timelineUpdate, 0, 1000);
    }

    private void setEndTime() {
        mediaPlayer.setOnReady(() -> {
            endTime = mediaPlayer.getMedia().getDuration().toSeconds();
            mainController.reloadTimeline();
        });
    }

    public double getEndTime() {
        return endTime;
    }

    public Song getCurrentSong() {
        return currentSong;
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

        mediaPlayer.setVolume(volume/2); // Scaled volume down
    }

    public void setTimeline(double time) {
        if (disabled()) {
            return;
        }

        mediaPlayer.seek(Duration.seconds(time));
        timeline = time;
    }

    public double getTimeline() {
        return timeline;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    private void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
        playModeAction();
    }

    public void togglePlayMode(){
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

    public static String formatTime(double time) {
        int minutes = (int) time / 60;
        int seconds = (int) time % 60;

        return String.format("%d:%02d", minutes, seconds);
    }
}
