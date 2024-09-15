import os
import random
from tkinter import HORIZONTAL

from pygame import mixer
import tkinter as tk

# Relative path to the music folder
playlists_path = os.path.join(os.path.dirname(__file__), "Music")

# Music files
playlist_dict, songs_dict, playlist_array, songs_array = {}, {}, [], []

# For Tkinter
pause_button = None
playback_button = None


# Initialize playlists and songs
def init_playlists_and_songs():
    playlist_count = 0
    playlists = os.listdir(playlists_path)

    for playlist in playlists:
        playlist_path = os.path.join(playlists_path, playlist)
        playlist_dict[playlist] = {}
        songs = os.listdir(playlist_path)

        for song in songs:
            preserved_song = song
            song = song.replace(".mp3", "") if song.endswith(".mp3") else song
            playlist_dict[playlist][song] = os.path.join(playlist_path, preserved_song)
            songs_dict[song] = os.path.join(playlist_path, preserved_song)
            songs_array.append(song)

        playlist_array.append(playlist)
        playlist_count += 1


# Class
class MusicPlayerState:
    def __init__(self):
        # volume
        self.music_volume = 0.3
        # playlist name
        self.current_playlist = ""
        # playing order options
        self.is_repeat_playlist = True
        self.is_shuffle_playlist = False
        self.is_repeat_song = False
        # playing order
        self.current_playing_index = 0
        self.shuffle_order = []
        self.playing_order = []
        # playing or not playing
        self.is_song_paused = False
        self.is_stopped_playing = True
        # song name and path
        self.current_song = None
        self.current_song_path = None

        # Initializing mixer and setting volume
        mixer.init()
        mixer.music.set_volume(self.music_volume)

    # Adjusts music player volume
    def control_volume(self, volume):
        self.music_volume = volume / 4
        mixer.music.set_volume(self.music_volume)

    # Loads and plays song (last step)
    def load_and_play(self, song_path):
        self.is_song_paused, self.is_stopped_playing = False, False
        mixer.music.load(song_path)
        mixer.music.play()

    # Plays next song
    def next_song(self):
        if self.is_repeat_song:
            self.load_and_play(self.current_song_path)
        else:
            self.advance_song()
            song_path = self.get_current_song_path()
            self.load_and_play(song_path)

    # Advances to the next song in the playlist or shuffle order
    def advance_song(self):
        if self.current_playing_index < len(self.playing_order):
            self.current_playing_index += 1
        else:
            self.current_playing_index = 0

    # Returns current song path
    def get_current_song_path(self):
        if self.is_shuffle_playlist:
            self.current_song = self.shuffle_order[self.current_playing_index]
        else:
            self.current_song = self.playing_order[self.current_playing_index]
        return playlist_dict[self.current_playlist][self.current_song]

    # Plays a specific song or playlist
    def play_music(self, song):
        self.current_song = song
        self.playing_order = list(playlist_dict[self.current_playlist].keys())
        self.current_playing_index = self.playing_order.index(song)
        self.current_song_path = self.get_current_song_path()
        self.load_and_play(self.current_song_path)

    def control_music(self, command, update_button_function=None):
        if command == "pause" and mixer.music.get_busy():
            self.is_song_paused = True
            mixer.music.pause()
            update_button_function()

        elif command == "unpause":
            self.is_song_paused = False
            mixer.music.unpause()
            update_button_function()

        elif command == "previous":
            pass

        elif command == "next":
            self.next_song()

        elif command == "repeat_song":
            self.is_repeat_song = True
            self.is_repeat_playlist = False
            self.is_shuffle_playlist = False
            update_button_function()

        elif command == "repeat_playlist":
            self.is_repeat_song = False
            self.is_repeat_playlist = True
            self.is_shuffle_playlist = False
            update_button_function()

        elif command == "shuffle_playlist":
            self.is_repeat_song = False
            self.is_repeat_playlist = False
            self.is_shuffle_playlist = True
            update_button_function()
            self.shuffle_playlist()

    # Shuffles current playlist
    def shuffle_playlist(self):
        self.shuffle_order = random.sample(self.playing_order, len(self.playing_order))


# Initializing Music Player
music_player = MusicPlayerState()


def init_tkinter():
    root = tk.Tk()

    root.geometry("1920x1080")
    root.state('zoomed')
    root.title("SharpPlay")

    # Set a theme color
    primary_color = "#d66dd6"
    secondary_color = "#f0f0f0"
    third_color = "#78d8f0"
    button_color = "#ffffff"
    button_hover_color = "#45a049"
    text_color = "#ffffff"
    background_color = "#333333"

    # Configure root background
    root.configure(bg=background_color)

    # Title Label
    label = tk.Label(root, text="SharpPlay", font=('Segoe UI', 35), fg=primary_color, bg=background_color)
    label.pack(pady=20)

    # Playlists Frame
    playlist_frame = tk.Frame(root, bg=background_color, bd=5, relief="raised")
    playlist_frame.place(x=20, y=100, width=300, height=800)  # Will add pages not scroll

    # Songs Section
    canvas = tk.Canvas(root, bg=background_color)
    canvas.place(x=340, y=100, width=1400, height=800)
    scrollbar = tk.Scrollbar(root, orient="vertical", command=canvas.yview)
    scrollbar.place(x=1740, y=100, height=800)
    songs_frame = tk.Frame(canvas, bg=background_color)
    canvas.create_window((100, 0), window=songs_frame, anchor="nw")
    canvas.config(yscrollcommand=scrollbar.set)
    scrollbar.config(command=canvas.yview)

    def update_scroll_region(event=None):
        canvas.update_idletasks()  # Ensure that the canvas updates
        canvas.config(scrollregion=canvas.bbox("all"))  # Set the scroll region to the bounding box of all content
    songs_frame.bind("<Configure>", update_scroll_region)

    # Songs Content
    def enter_playlist(playlist):
        music_player.current_playlist = playlist

        for widget in songs_frame.winfo_children():
            widget.destroy()

        for song in playlist_dict[playlist]:
            song_button = tk.Button(songs_frame, text=song, font=('Segoe UI', 16), width=100, height=2,
                                    bg=secondary_color, fg=primary_color, activebackground=button_hover_color,
                                    command=lambda s=song: music_player.play_music(s))
            song_button.pack(pady=5)

    # Playlist Content
    for index, playlist in enumerate(playlist_array, start=1):
        playlist_button = tk.Button(playlist_frame, text=playlist, font=('Segoe UI', 18), width=20, height=2,
                                    bg=third_color, fg=text_color, activebackground=button_hover_color,
                                    command=lambda p=playlist: enter_playlist(p))

        playlist_button.pack(pady=10)

        if index == 1:
            enter_playlist(playlist)

    # Music Controls Frame
    control_frame = tk.Frame(root, bg=background_color)
    control_frame.pack(side=tk.BOTTOM, fill=tk.X, pady=20)

    previous_button = tk.Button(control_frame, text="Previous", font=('Segoe UI', 16), width=10, height=1,
                                bg=primary_color, fg=text_color, activebackground=button_hover_color,
                                command=lambda c="previous": music_player.control_music(c))
    previous_button.pack(side=tk.LEFT, padx=10)

    def update_pause_button():
        global pause_button

        if pause_button:
            # Update the existing button instead of creating a new one
            if music_player.is_song_paused:
                pause_button.config(text="Unpause", command=lambda c="unpause": music_player.control_music(c, update_pause_button))
            else:
                pause_button.config(text="Pause", command=lambda c="pause": music_player.control_music(c, update_pause_button))
        else:
            # Initialize the button the first time
            pause_button = tk.Button(control_frame, text="Pause", font=('Segoe UI', 16), width=10, height=1,
                                     bg=primary_color, fg=text_color, activebackground=button_hover_color,
                                     command=lambda c="pause": music_player.control_music(c, update_pause_button))
            pause_button.pack(side=tk.LEFT, padx=10)

    update_pause_button()

    next_button = tk.Button(control_frame, text="Next", font=('Segoe UI', 16), width=10, height=1,
                            bg=primary_color, fg=text_color, activebackground=button_hover_color,
                            command=lambda c="next": music_player.control_music(c))
    next_button.pack(side=tk.LEFT, padx=10)

    volume_slider = tk.Scale(control_frame, bg=primary_color, fg=text_color,
                             activebackground=button_hover_color,
                             from_=0, to=100, orient=HORIZONTAL,
                             command=lambda v: music_player.control_volume(float(v) / 100))
    volume_slider.pack(side=tk.LEFT, padx=10)
    volume_slider.set(music_player.music_volume * 100)

    def update_playback_button():
        global playback_button

        if playback_button:
            # Update the existing button instead of creating a new one
            if music_player.is_repeat_song:
                playback_button.config(text="Repeating Song",
                                       command=lambda c="repeat_playlist": music_player.control_music(c, update_playback_button))
            elif music_player.is_repeat_playlist:
                playback_button.config(text="Repeating Playlist",
                                       command=lambda c="shuffle_playlist": music_player.control_music(c, update_playback_button))
            elif music_player.is_shuffle_playlist:
                playback_button.config(text="Shuffling Playlist",
                                       command=lambda c="repeat_song": music_player.control_music(c, update_playback_button))
        else:
            # Initialize the button the first time
            playback_button = tk.Button(control_frame, text="Repeating Playlist", font=('Segoe UI', 16), width=20, height=1,
                                        bg=primary_color, fg=text_color, activebackground=button_hover_color,
                                        command=lambda c="shuffle_playlist": music_player.control_music(c, update_playback_button))
            playback_button.pack(side=tk.LEFT, padx=10)

    update_playback_button()

    background_loop(root)

    root.mainloop()


def background_loop(root):
    if not mixer.music.get_busy() and not music_player.is_song_paused and not music_player.is_stopped_playing:
        music_player.next_song()

    root.after(1000, background_loop, root)


# Main loop
def main():
    init_playlists_and_songs()
    init_tkinter()


if __name__ == "__main__":
    main()
