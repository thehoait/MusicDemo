package com.example.asiantech.musicdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class Artist {
    private long artistId;
    private String artist;
}
