package com.example.student.musicplayer;

import android.graphics.Bitmap;

import java.io.File;

public class SongObject {

    public File songFile;
    public String title;
    public String artist;
    public Bitmap image;

    public SongObject(File songFile, String title, String artist, Bitmap image) {
        this.songFile = songFile;
        this.title = title;
        this.artist = artist;
        this.image = image;
    }
}
