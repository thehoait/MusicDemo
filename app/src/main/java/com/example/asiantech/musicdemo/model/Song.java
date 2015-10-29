package com.example.asiantech.musicdemo.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class Song implements Serializable{
    private long id;
    private String title;
    private String artist;
    private boolean isPlaying;
}
