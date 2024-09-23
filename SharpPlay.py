import os
import random
from tkinter import HORIZONTAL

from pygame import mixer
import tkinter as tk

# Relative path to the music folder
playlists_path = os.path.join(os.path.dirname(__file__), "Music")

# Music files
playlist_dict, songs_dict, playlist_array, songs_array = {}, {}, [], []


# Initialize playlists and songs
def init_playlists_and_songs():
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


# Class
class MusicPlayerState:
    def __init__(self):
        # volume
        self.music_volume = 0.3
        # in playlist
        self.in_playlist = ""
        # playing order options
        self.is_repeat_playlist = True
        self.is_shuffle_playlist = False
        self.is_super_shuffle_playlist = False
        self.is_repeat_song = False
        # playing
        self.current_playlist = ""
        self.current_playing_index = 0
        self.current_order = []
        # playing or not playing
        self.is_song_paused = False
        self.is_stopped_playing = True
        # song name and path
        self.current_song = None
        self.current_song_path = None
        # Tkinter buttons
        self.pause_button = None
        self.playback_button = None

        # Initializing mixer and setting volume
        mixer.init()
        mixer.music.set_volume(self.music_volume)

    # Adjusts music player volume
    def control_volume(self, volume):
        self.music_volume = volume / 4
        mixer.music.set_volume(self.music_volume)

    # Loads and plays song
    def load_and_play(self, song_path):
        self.is_song_paused, self.is_stopped_playing = False, False
        self.update_pause_button()
        mixer.music.load(song_path)
        mixer.music.play()

    # Helper function to get the next song index
    def get_next_song_index(self):
        return (self.current_playing_index + 1) % len(self.current_order)

    # Helper function to get the previous song index
    def get_previous_song_index(self):
        return (self.current_playing_index - 1) % len(self.current_order)

    # Plays next song
    def next_song(self):
        if self.is_repeat_song:
            self.load_and_play(self.current_song_path)
        else:
            self.advance_song()
            self.load_and_play(self.current_song_path)

    # Plays previous song
    def previous_song(self):
        if self.is_repeat_song:
            self.load_and_play(self.current_song_path)
        else:
            self.reverse_song()
            self.load_and_play(self.current_song_path)

    # Advances to the next song
    def advance_song(self):
        self.current_playing_index = self.get_next_song_index()
        self.current_song = self.current_order[self.current_playing_index]
        self.current_song_path = playlist_dict[self.current_playlist][self.current_song]

    # Reverses to the prev song
    def reverse_song(self):
        self.current_playing_index = self.get_previous_song_index()
        self.current_song = self.current_order[self.current_playing_index]
        self.current_song_path = playlist_dict[self.current_playlist][self.current_song]

    # Plays a specific song or playlist
    def play_music(self, song):
        self.current_song = song
        self.current_playlist = self.in_playlist
        self.current_song_path = playlist_dict[self.current_playlist][song]
        if self.is_shuffle_playlist:
            self.shuffle_playlist()
        elif self.is_repeat_playlist:
            self.repeat_playlist()
        self.load_and_play(self.current_song_path)

    # Shuffles current playlist
    def shuffle_playlist(self):
        remaining_songs = [s for s in self.current_order if s != self.current_song]
        random.shuffle(remaining_songs)
        self.current_order = remaining_songs
        self.current_order.insert(0, self.current_song)
        self.current_playing_index = 0

    def repeat_playlist(self):
        self.current_order = list(playlist_dict[self.current_playlist].keys())
        self.current_playing_index = self.current_order.index(self.current_song)

    def control_music(self, command):
        if command == "pause" and mixer.music.get_busy():
            self.is_song_paused = True
            mixer.music.pause()
            self.update_pause_button()

        elif command == "unpause":
            self.is_song_paused = False
            mixer.music.unpause()
            self.update_pause_button()

        elif command == "previous":
            self.previous_song()

        elif command == "next":
            self.next_song()

        elif command == "repeat_song":
            self.is_repeat_song = True
            self.is_repeat_playlist = False
            self.is_shuffle_playlist = False
            self.update_playback_button()

        elif command == "repeat_playlist":
            self.is_repeat_song = False
            self.is_repeat_playlist = True
            self.is_shuffle_playlist = False
            self.repeat_playlist()
            self.update_playback_button()

        elif command == "shuffle_playlist":
            self.is_repeat_song = False
            self.is_repeat_playlist = False
            self.is_shuffle_playlist = True
            self.shuffle_playlist()
            self.update_playback_button()

        elif command == "super_shuffle_playlist":
            self.is_repeat_song = False
            self.is_repeat_playlist = False
            self.is_shuffle_playlist = False
            self.is_super_shuffle_playlist = True
            self.shuffle_playlist()
            self.update_playback_button()

    def update_playback_button(self):
        if self.is_repeat_song:
            self.playback_button.config(text="Repeating Song",
                                        command=lambda c="repeat_playlist": self.control_music(c))
        elif self.is_repeat_playlist:
            self.playback_button.config(text="Repeating Playlist",
                                        command=lambda c="shuffle_playlist": self.control_music(c))
        elif self.is_shuffle_playlist:
            self.playback_button.config(text="Shuffling Playlist", command=lambda c="repeat_song": self.control_music(c))

    def update_pause_button(self):
        if self.is_song_paused:
            self.pause_button.config(text="Unpause", command=lambda c="unpause": self.control_music(c))
        else:
            self.pause_button.config(text="Pause", command=lambda c="pause": self.control_music(c))


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
        music_player.in_playlist = playlist

        for widget in songs_frame.winfo_children():
            widget.destroy()

        for song in playlist_dict[playlist]:
            song_button = tk.Button(songs_frame, text=song, font=('Segoe UI', 16), width=100, height=2,
                                    bg=secondary_color, fg=primary_color, activebackground=button_hover_color,
                                    command=lambda s=song: music_player.play_music(s))
            song_button.pack(pady=5)

        canvas.yview_moveto(0)

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

    # Initialize the button the first time
    music_player.pause_button = tk.Button(control_frame, text="Pause", font=('Segoe UI', 16), width=10, height=1,
                             bg=primary_color, fg=text_color, activebackground=button_hover_color,
                             command=lambda c="pause": music_player.control_music(c))
    music_player.pause_button.pack(side=tk.LEFT, padx=10)

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

    # Initialize the button the first time
    music_player.playback_button = tk.Button(control_frame, text="Repeating Playlist", font=('Segoe UI', 16), width=20,
                                             height=1,
                                             bg=primary_color, fg=text_color, activebackground=button_hover_color,
                                             command=lambda c="shuffle_playlist": music_player.control_music(c))
    music_player.playback_button.pack(side=tk.LEFT, padx=10)

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
