package com.example.slideshow;

import javafx.scene.image.Image;
import java.io.File;

public interface Iterator {
    boolean hasNext();
    Image next();
    boolean hasPrevious();
    Image previous();
    Image first();
    Image last();
    int getCurrentIndex();
    File getCurrentFile();
}