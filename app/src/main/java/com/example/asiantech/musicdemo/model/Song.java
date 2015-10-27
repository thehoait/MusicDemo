package com.example.asiantech.musicdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class Song {
    private long id;
    private String title;
    private String artist;
    private boolean isPlaying;
}
