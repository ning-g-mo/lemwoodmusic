package com.example.lemwoodmusic;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private long id;
    private String name;
    private List<Song> songs;

    public Playlist(long id, String name) {
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
        }
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }

    public void clearSongs() {
        songs.clear();
    }

    public int getSongCount() {
        return songs.size();
    }
}