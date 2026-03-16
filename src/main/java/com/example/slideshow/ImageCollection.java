package com.example.slideshow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.NoSuchElementException;

public class ImageCollection implements Aggregate {
    private File[] files;

    public ImageCollection(File directory) {
        // Получаем все файлы с заданным расширением
        this.files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                        lower.endsWith(".png") || lower.endsWith(".gif") ||
                        lower.endsWith(".bmp");
            }
        });

        if (files == null) {
            files = new File[0];
        }

    }

    public Iterator getIterator() {
        return new ImageFileIterator();
    }

    // Метод для получения файла по индексу (для навигации)
    public File getFile(int index) {
        if (index >= 0 && index < files.length) {
            return files[index];
        }
        return null;
    }

    public int size() {
        return files.length;
    }

    // Внутренний класс итератора
    private class ImageFileIterator implements Iterator {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return files.length > 0;
        }

        @Override
        public Object next() {
            if (files.length == 0) return null;
            currentIndex = (currentIndex + 1) % files.length;
            return files[currentIndex];
        }

        @Override
        public Object preview() {
            if (files.length == 0) return null;
            currentIndex = (currentIndex - 1 + files.length) % files.length;
            return files[currentIndex];
        }

        // Дополнительные методы для навигации
        public boolean hasPreview() {
            return files.length > 0;
        }

        public void reset() {
            currentIndex = 0;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        // добавляем метод для получения текущего файла
        public File getCurrentFile() {
            if (files.length == 0) return null;
            return files[currentIndex];
        }
    }
}