package com.example.slideshow;

import javafx.scene.image.Image;
import java.io.File;

public class ImageLoader {
    // Загрузка изображения из файла
    public Image loadFromFile(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            return new Image(file.toURI().toString());
        } catch (Exception e) {
            return null;
        }
    }

    // Загрузка из ресурсов (для тестирования)
    public Image loadFromResource(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            return null;
        }
    }
}