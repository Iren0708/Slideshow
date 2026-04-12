package com.example.slideshow;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.animation.*;
import javafx.util.Duration;
import java.io.File;

public class Controller {

    @FXML private ImageView imageView;
    @FXML private Label counterLabel;
    @FXML private ComboBox<String> filterBox;
    @FXML private ComboBox<String> effectBox;
    @FXML private TextArea infoArea;
    @FXML private Button playButton;

    private ImageCollection collection;
    private Iterator iterator;
    private ImageLoader loader;
    private File currentFolder;
    private Timeline autoPlayTimeline;
    private boolean isAutoPlaying = false;

    @FXML
    public void initialize() {
        loader = new ImageLoader();
        currentFolder = new File("photos");

        // Заполнение комбобоксов
        filterBox.getItems().addAll("Все", ".jpeg", ".png", ".gif");
        filterBox.setValue("Все");
        effectBox.getItems().addAll("Нет эффекта", "Исчезание", "Масштабирование");
        effectBox.setValue("Нет эффекта");

        // Загружаем коллекцию с начальным фильтром
        collection = new ImageCollection(currentFolder, filterBox.getValue());
        iterator = collection.getIterator();
        showCurrentImage();

        // Обработчики смены фильтра и эффекта
        filterBox.setOnAction(e -> applyFilter());
        effectBox.setOnAction(e -> { /* просто сохраняем выбор, применяется при смене картинки */ });
    }

    private void applyFilter() {
        if (isAutoPlaying) stopAutoPlay();
        String selectedFilter = filterBox.getValue();
        collection.setFilter(currentFolder, selectedFilter);
        iterator = collection.getIterator();
        if (collection.size() > 0) {
            Image img = iterator.first();
            if (img != null) imageView.setImage(img);
        } else {
            imageView.setImage(null);
        }
        updateCounterAndInfo();
    }

    private void showCurrentImage() {
        if (collection.size() == 0) {
            imageView.setImage(null);
            counterLabel.setText("0 из 0");
            infoArea.setText("Нет изображений в папке 'photos'");
            return;
        }
        File currentFile = iterator.getCurrentFile();
        Image img = loader.loadFromFile(currentFile);
        applyTransition(img);
        updateCounterAndInfo();
    }

    private void applyTransition(Image newImage) {
        String effect = effectBox.getValue();
        if (effect == null || effect.equals("Нет эффекта")) {
            imageView.setImage(newImage);
            return;
        }
        // Для плавности: сначала устанавливаем новое изображение, затем запускаем анимацию появления
        imageView.setImage(newImage);
        Transition transition = null;
        switch (effect) {
            case "Исчезание":
                FadeTransition ft = new FadeTransition(Duration.millis(500), imageView);
                ft.setFromValue(0);
                ft.setToValue(1);
                transition = ft;
                break;
            case "Масштабирование":
                ScaleTransition st = new ScaleTransition(Duration.millis(500), imageView);
                st.setFromX(0.8);
                st.setFromY(0.8);
                st.setToX(1);
                st.setToY(1);
                transition = st;
                break;
        }
        if (transition != null) {
            transition.play();
        }
    }

    private void updateCounterAndInfo() {
        int size = collection.size();
        if (size == 0) {
            counterLabel.setText("0 из 0");
            infoArea.setText("Нет изображений");
            return;
        }
        int index = iterator.getCurrentIndex() + 1;
        counterLabel.setText(index + " из " + size);
        // Информация о файле + EXIF
        File file = iterator.getCurrentFile();
        if (file != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Имя: ").append(file.getName()).append("\n");
            sb.append("Размер: ").append(file.length() / 1024).append(" КБ\n");
            sb.append("Путь: ").append(file.getAbsolutePath()).append("\n");
            sb.append("EXIF:\n").append(ExifReader.getExifInfo(file));
            infoArea.setText(sb.toString());
        } else {
            infoArea.setText("Нет данных о файле");
        }
    }

    // Навигация
    @FXML
    private void next() {
        if (isAutoPlaying) stopAutoPlay();
        if (collection.size() == 0) return;
        Image img = iterator.next();
        if (img != null) applyTransition(img);
        updateCounterAndInfo();
    }

    @FXML
    private void prev() {
        if (isAutoPlaying) stopAutoPlay();
        if (collection.size() == 0) return;
        Image img = iterator.previous();
        if (img != null) applyTransition(img);
        updateCounterAndInfo();
    }

    @FXML
    private void first() {
        if (isAutoPlaying) stopAutoPlay();
        if (collection.size() == 0) return;
        Image img = iterator.first();
        if (img != null) applyTransition(img);
        updateCounterAndInfo();
    }

    @FXML
    private void last() {
        if (isAutoPlaying) stopAutoPlay();
        if (collection.size() == 0) return;
        Image img = iterator.last();
        if (img != null) applyTransition(img);
        updateCounterAndInfo();
    }

    // Автоматический режим (слайд-шоу)
    @FXML
    private void toggleAutoPlay() {
        if (isAutoPlaying) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }

    private void startAutoPlay() {
        if (collection.size() == 0) return;
        isAutoPlaying = true;
        playButton.setText("⏸ Pause");
        autoPlayTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            if (collection.size() > 0) {
                Image img = iterator.next();
                if (img != null) applyTransition(img);
                updateCounterAndInfo();
            }
        }));
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);
        autoPlayTimeline.play();
    }

    private void stopAutoPlay() {
        if (autoPlayTimeline != null) {
            autoPlayTimeline.stop();
        }
        isAutoPlaying = false;
        playButton.setText("▶ Play");
    }
}