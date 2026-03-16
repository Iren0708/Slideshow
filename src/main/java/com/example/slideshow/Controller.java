package com.example.slideshow;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import java.io.File;

public class Controller {
    @FXML
    private ImageView imageView;
    @FXML
    private Label counterLabel;
    @FXML
    private ComboBox<String> filterBox;

    private ImageCollection collection;
    private Iterator iterator;
    private ImageLoader loader;
    private int currentIndex;

    @FXML
    public void initialize() {
        loader = new ImageLoader();
        File folder = new File("photos");
        collection = new ImageCollection(folder);
        iterator = collection.getIterator();
        currentIndex = 0;

        filterBox.getItems().addAll("все", "jpg", "png", "gif");
        filterBox.setValue("все");

        showCurrentImage();
    }

    @FXML
    private void onNext() {
        if (collection.size() == 0) return;
        iterator.next();
        currentIndex = (currentIndex + 1) % collection.size();
        showCurrentImage();
    }

    @FXML
    private void onPrev() {
        if (collection.size() == 0) return;
        iterator.preview();
        currentIndex = (currentIndex - 1 + collection.size()) % collection.size();
        showCurrentImage();
    }

    @FXML
    private void onFirst() {
        if (collection.size() == 0) return;
        currentIndex = 0;
        iterator = collection.getIterator();
        showCurrentImage();
    }

    @FXML
    private void onLast() {
        if (collection.size() == 0) return;
        currentIndex = collection.size() - 1;
        for (int i = 0; i < currentIndex; i++) {
            iterator.next();
        }
        showCurrentImage();
    }

    private void showCurrentImage() {
        File currentFile = collection.getFile(currentIndex);
        if (currentFile != null) {
            imageView.setImage(loader.loadFromFile(currentFile));
        }

        int total = collection.size();
        if (total == 0) {
            counterLabel.setText("нет картинок");
        } else {
            counterLabel.setText((currentIndex + 1) + " из " + total);
        }
    }
}