import os
import random
from pygame import mixer

# Initialize mixer
mixer.init()

# Setting up global variables
musicVol = 0.3
mixer.music.set_volume(musicVol)

# Relative path to the music folder
playlists_path = os.path.join(os.path.dirname(__file__), "Music")

# Global Variables
playlistDic, songsDic, playlistArrayWPlay, songsArrayWPlay = {}, {}, [], []
shuffleOrder, shuffle = [], False
playingOrder, repeat = 0, False
songPaused, stoppedPlaying = False, False
selectedPlaylistOrSong, whichSong, value = None, None, None

# Initialize playlists and songs
def init_playlists_and_songs():
    playlistCount = 0
    playlistsPath = os.listdir(playlists_path)

    for playlist in playlistsPath:
        playlist_path = os.path.join(playlists_path, playlist)
        playlistDic[playlist.lower()] = {}
        songs = os.listdir(playlist_path)

        for song in songs:
            preservedSong = song
            song = song.replace(".mp3", "") if song.endswith(".mp3") else song
            playlistDic[playlist.lower()][song.lower()] = os.path.join(playlist_path, preservedSong)
            songsDic[song.lower()] = os.path.join(playlist_path, preservedSong)
            songsArrayWPlay.append("play " + song.lower())

        playlistArrayWPlay.append("play " + playlist.lower())
        playlistCount += 1

# Function to load and play a song
def load_and_play(song_path):
    mixer.music.load(song_path)
    mixer.music.play()

# Adjust volume
def adjust_volume(command):
    global musicVol
    volumeControlDic = {
        "loud": 0.2, "quiet": -0.2, "louder": 0.1, "quieter": -0.1,
        "little louder": 0.05, "little quieter": -0.05
    }

    if command in volumeControlDic:
        musicVol += volumeControlDic[command]
        musicVol = max(0, musicVol)
        mixer.music.set_volume(musicVol)

# Play the next song in shuffle mode
def play_next_song():
    global playingOrder, whichSong, value
    if playingOrder < len(shuffleOrder):
        whichSong = shuffleOrder[playingOrder]
        playingOrder += 1
    else:
        playingOrder = 0
        whichSong = shuffleOrder[playingOrder]
        playingOrder += 1

    value = list(playlistDic[selectedPlaylistOrSong].values())[whichSong]
    load_and_play(value)

# Play a specific song or playlist
def play_music(command):
    global stoppedPlaying, songPaused, shuffle, repeat, selectedPlaylistOrSong, whichSong, value, playingOrder

    stoppedPlaying, songPaused = False, False

    if command in playlistArrayWPlay:
        selectedPlaylistOrSong = command[5:]
        shuffle = True
        repeat = False
        shuffleOrder.clear()

        playlistLen = len(playlistDic[selectedPlaylistOrSong])
        for _ in range(playlistLen):
            while True:
                randomNum = random.randrange(playlistLen)
                if randomNum not in shuffleOrder:
                    shuffleOrder.append(randomNum)
                    break

        playingOrder = 0
        play_next_song()

    elif command in songsArrayWPlay:
        selectedPlaylistOrSong = command[5:]
        whichSong = selectedPlaylistOrSong
        shuffle = False
        repeat = True

        value = songsDic.get(whichSong)
        load_and_play(value)

# Pause, unpause, or stop music
def control_music(command):
    global songPaused, stoppedPlaying

    if command == "pause" and mixer.music.get_busy():
        mixer.music.pause()
        songPaused = True

    elif command == "unpause":
        mixer.music.unpause()
        songPaused = False

# Main loop
def main():
    init_playlists_and_songs()

    while True:
        command = input("Enter command: ").lower()

        if command == "quit" or command == "exit":
            break

        elif command in ["pause", "unpause"]:
            control_music(command)

        elif command in songsArrayWPlay or command in playlistArrayWPlay:
            play_music(command)

        elif command == "next" and shuffle:
            play_next_song()

        elif command == "repeat" and repeat:
            load_and_play(value)

        else:
            adjust_volume(command)

if __name__ == "__main__":
    main()
