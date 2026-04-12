package com.example.slideshow;
import javafx.scene.image.Image;
import java.io.File;

public class ImageLoader {
    public Image loadFromFile(File file) {
        if (file == null || !file.exists()) return null;
        try {
            return new Image(file.toURI().toString());
        } catch (Exception e) {
            return null;
        }
    }
}