package com.example.slideshow;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import java.io.File;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.control.TextArea;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import javafx.scene.image.Image;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Controller {
    @FXML
    private ImageView imageView;
    @FXML
    private Label counterLabel;
    @FXML
    private ComboBox<String> filterBox;
    @FXML
    private TextArea infoArea;
    @FXML
    private ComboBox<String> effectBox;

    private String currentEffect = "исчезание";

    private ImageCollection collection;
    private Iterator iterator;
    private ImageLoader loader;
    private int currentIndex;
    private Timeline timeline;
    private boolean isPlaying = false;


    @FXML
    public void initialize() {
        loader = new ImageLoader();
        File folder = new File(getClass().getResource("/photos").getPath());
        collection = new ImageCollection(folder);
        iterator = collection.getIterator();
        currentIndex = 0;

        // фильтры
        filterBox.getItems().addAll("все", "jpg", "png", "gif");
        filterBox.setValue("все");

        // эффекты
        effectBox.getItems().addAll("исчезание", "сдвиг", "масштаб", "поворот");
        effectBox.setValue("исчезание");
        timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> onNext()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        showCurrentImage();
    }
    @FXML
    private void onEffectChange() {
        currentEffect = effectBox.getValue();

    }

    @FXML
    private void onNext() {
        if (isPlaying) {
            timeline.stop();
            isPlaying = false;
        }
        if (collection.size() == 0) return;
        iterator.next();
        currentIndex = (currentIndex + 1) % collection.size();
        showCurrentImage();
    }

    @FXML
    private void onPrev() {
        if (isPlaying) {
            timeline.stop();
            isPlaying = false;
        }
        if (collection.size() == 0) return;
        iterator.preview();
        currentIndex = (currentIndex - 1 + collection.size()) % collection.size();
        showCurrentImage();
    }

    @FXML
    private void onFirst() {
        if (isPlaying) {
            timeline.stop();
            isPlaying = false;
        }
        if (collection.size() == 0) return;
        currentIndex = 0;
        iterator = collection.getIterator();
        showCurrentImage();
    }

    @FXML
    private void onLast() {
        if (isPlaying) {
            timeline.stop();
            isPlaying = false;
        }
        if (collection.size() == 0) return;
        currentIndex = collection.size() - 1;
        for (int i = 0; i < currentIndex; i++) {
            iterator.next();
        }
        showCurrentImage();
    }

    @FXML
    private void onFilterChange() {
        String selected = filterBox.getValue();
        if (selected == null) return;

        collection.setFilter(selected);
        iterator = collection.getIterator();
        currentIndex = 0;
        showCurrentImage();
    }
    private void showCurrentImage() {
        int total = collection.size();

        if (total == 0) {
            imageView.setImage(null);
            counterLabel.setText("нет картинок");
            infoArea.setText("");
            return;
        }

        File currentFile = collection.getFile(currentIndex);
        if (currentFile != null) {
            applyEffect(currentFile);  // вместо прямой загрузки
        }

        counterLabel.setText((currentIndex + 1) + " из " + total);
        showFileInfo(currentFile);     // добавляем информацию
    }

    private void applyEffect(File file) {
        if (file == null) return;

        Image newImage = loader.loadFromFile(file);
        if (newImage == null) return;

        imageView.setImage(newImage);

        // останавливаем старые анимации
        imageView.getTransforms().clear();

        // выбираем эффект
        switch (currentEffect) {
            case "исчезание":
                FadeTransition fade = new FadeTransition(Duration.millis(500), imageView);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
                break;

            case "сдвиг":
                TranslateTransition move = new TranslateTransition(Duration.millis(500), imageView);
                move.setFromX(-50);
                move.setToX(0);
                move.play();
                break;

            case "масштаб":
                ScaleTransition scale = new ScaleTransition(Duration.millis(500), imageView);
                scale.setFromX(0.5);
                scale.setFromY(0.5);
                scale.setToX(1);
                scale.setToY(1);
                scale.play();
                break;

            case "поворот":
                RotateTransition rotate = new RotateTransition(Duration.millis(500), imageView);
                rotate.setFromAngle(-90);
                rotate.setToAngle(0);
                rotate.play();
                break;
        }
    }
    private void showFileInfo(File file) {
        if (file == null) {
            infoArea.setText("");
            return;
        }

        StringBuilder info = new StringBuilder();
        info.append("имя: ").append(file.getName()).append("\n");
        info.append("размер: ").append(file.length() / 1024).append(" кб\n");

        // пробуем прочитать exif
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory exif = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (exif != null) {
                java.util.Date date = exif.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (date != null) {
                    info.append("снято: ").append(date).append("\n");
                }

                String make = exif.getString(ExifSubIFDDirectory.TAG_MAKE);
                String model = exif.getString(ExifSubIFDDirectory.TAG_MODEL);
                if (make != null && model != null) {
                    info.append("камера: ").append(make).append(" ").append(model).append("\n");
                }
            }
        } catch (Exception e) {
            // нет exif - ничего страшного
        }

        infoArea.setText(info.toString());
    }
}