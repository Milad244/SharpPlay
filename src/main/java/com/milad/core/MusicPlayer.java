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

/**
 * Manages audio playback and updates the GUI accordingly.
 */
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

    /**
     * Constructor for music player. Initializes the DB and the playingOrder Arraylist.
     * Starts the music player paused and on ONE_LOOP play mode.
     */
    public MusicPlayer() {
        db = DatabaseHandler.getHandler();
        playingOrder = new ArrayList<>();
    }

    /**
     * Starts the music player paused and on ONE_LOOP play mode.
     * Cannot be called inside constructor because methods will try to access itself, but it won't be initialized yet.
     */
    public void startWithDefaultSettings() {
        setPausedState(true);
        setPlayMode(PlayMode.ONE_LOOP);
    }

    /**
     * Checks if a song(mediaPlayer) is loaded.
     * @return returns true if song is not loaded, returns false if song is loaded
     */
    public boolean disabled() {
        return mediaPlayer == null;
    }

    /**
     * Sets up a playing order from the new songs playlist, and plays the new song.
     * @param playlist the playlist from which the song to be played is from, as a Playlist type
     * @param song the song to be played, as a Song type
     */
    public void playNewSong(Playlist playlist, Song song) {
        currentPlaylist = playlist;
        setPlayingOrder(playlist, song);
        playSong(song);
    }

    /**
     * Sets up the playing order for a given playlist and song based on what the playMode is.
     * @param playlist the playlist from which the song to be played is from, as a Playlist type
     * @param song the song to be played, as a Song type
     */
    private void setPlayingOrder(Playlist playlist, Song song) {
        switch (playMode) {
            case ONE_LOOP:
                playingOrder.clear();
                playingOrder.add(song);
                break;
            case LOOP:
                playingOrder = new ArrayList<>(playlist.getSongs()); // new so that we don't affect the reference
                break;
            case SHUFFLE:
                playingOrder = new ArrayList<>(playlist.getSongs()); // new so that we don't affect the reference
                Collections.shuffle(playingOrder);
        }
    }

    /**
     * Plays the next song in the playing order.
     */
    public void playNextSong() {
        if (disabled()) {
            return;
        }

        int currentIndex = playingOrder.indexOf(currentSong);
        Song nextSong;

        // If at the end of the playingOrder, go back to beginning
        if (playingOrder.size() > currentIndex + 1) {
            nextSong = playingOrder.get(currentIndex + 1);
        } else {
            nextSong = playingOrder.getFirst();
        }
        playSong(nextSong);
    }

    /**
     * Plays the previous song in the playing order.
     */
    public void playPrevSong() {
        if (disabled()) {
            return;
        }

        int currentIndex = playingOrder.indexOf(currentSong);
        Song prevSong;

        // If at the start of the playingOrder, go to the end
        if (currentIndex - 1 >= 0) {
            prevSong = playingOrder.get(currentIndex - 1);
        } else {
            prevSong = playingOrder.getLast();
        }
        playSong(prevSong);
    }

    /**
     * Sets the mediaPlayer to the given song and plays it. Also, sets up its end function and timeline.
     * @param song the song to be played, as a Song type
     */
    private void playSong(Song song) {
        currentSong = song;
        File songFile = new File(currentSong.getFile());
        Media songMedia = new Media(songFile.toURI().toString());

        // Removing previous music player
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        mediaPlayer = new MediaPlayer(songMedia);
        mediaPlayer.play();

        // When song ends, add to DB that the song was played and go the next song
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

    /**
     * Starts the timeline for the new song using a new timer.
     */
    private void startTimeline() {
        setEndTime();

        // Remove previous timeline timer
        if (timelineTimer != null) {
            timelineTimer.cancel();
        }

        // Creates new timer that updates the timeline each second
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

    /**
     * Sets the end time of the current song once the media is ready and updates the GUI accordingly.
     */
    private void setEndTime() {
        mediaPlayer.setOnReady(() -> { // Ensures we don't access media before it is fully loaded
            endTime = mediaPlayer.getMedia().getDuration().toSeconds();
            mainController.reloadTimeline();
        });
    }

    /**
     * Gets the end time of the current song.
     * @return the end time of the current song, as a double
     */
    public double getEndTime() {
        return endTime;
    }

    /**
     * Gets the current song being played.
     * @return the current song being played, as a Song type
     */
    public Song getCurrentSong() {
        return currentSong;
    }

    /**
     * Sets the paused state and then applies changes in pauseAction method.
     * @param paused if true then pause, if false then unpause.
     */
    private void setPausedState(boolean paused) {
        this.paused = paused;
        pauseAction();
    }

    /**
     * Toggles the paused state. If paused then unpauses, and vice versa. Then applies changes in pauseAction method.
     */
    public void togglePausedState() {
        paused = !paused;

        pauseAction();
    }

    /**
     * Pauses or unpauses current song based on paused field and updates GUI.
     */
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

    /**
     * Gets if the song is currently paused.
     * @return returns true if paused, returns false if playing
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets the volume of the music, scaled down.
     * @param volume the volume to set the music, as a double
     */
    public void setVolume(double volume) {
        this.volume = volume;

        if (disabled()) {
            return;
        }

        mediaPlayer.setVolume(volume/2); // Scaled volume down
    }

    /**
     * Sets the timeline and song time to a given time.
     * @param time the time of the song that wants to be skipped to, as a double
     */
    public void setTimeline(double time) {
        if (disabled()) {
            return;
        }

        mediaPlayer.seek(Duration.seconds(time));
        timeline = time;
    }

    /**
     * Gets the current time of the song/timeline.
     * @return the timeline, as a double
     */
    public double getTimeline() {
        return timeline;
    }

    /**
     * Gets the current play mode.
     * @return the playMode, as a PlayMode enum
     */
    public PlayMode getPlayMode() {
        return playMode;
    }

    /**
     * Sets the current play mode and updates the playing mode.
     * @param playMode the playMode, as a PlayMode enum
     */
    private void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
        playModeAction();
    }

    /**
     * Toggles the play mode and updates the playing mode.
     */
    public void togglePlayMode(){
        playMode = playMode.toggle();
        playModeAction();
    }

    /**
     * Reloads the play mode GUI and updates the playing order.
     */
    private void playModeAction() {
        mainController.reloadPlayMode();

        if (disabled()) {
            return;
        }
        setPlayingOrder(currentPlaylist, currentSong);
    }

    /**
     * Formats a time to look like min:second, such as 2:59.
     * @param time the time to be formatted, as a double
     * @return the formatted time, as a String
     */
    public static String formatTime(double time) {
        int minutes = (int) time / 60;
        int seconds = (int) time % 60;

        return String.format("%d:%02d", minutes, seconds);
    }
}
