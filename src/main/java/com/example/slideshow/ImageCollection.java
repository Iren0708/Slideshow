package com.example.slideshow;

import java.io.File;
import java.io.FilenameFilter;
import javafx.scene.image.Image;
import java.util.Arrays;
import java.util.Comparator;

public class ImageCollection implements Aggregate {
    private File[] files;
    private ImageLoader loader;
    private String currentFilter;

    public ImageCollection(File directory, String filterExtension) {
        loader = new ImageLoader();
        this.currentFilter = filterExtension;
        reloadFiles(directory, filterExtension);
    }

    private void reloadFiles(File directory, String filterExtension) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            files = new File[0];
            return;
        }

        String[] allowedExtensions;
        if (filterExtension == null || filterExtension.equals("Все")) {
            allowedExtensions = new String[]{".jpeg", ".jpg", ".png", ".gif"};
        } else {
            allowedExtensions = new String[]{filterExtension.toLowerCase()};
        }

        File[] rawFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lower = name.toLowerCase();
                for (String ext : allowedExtensions) {
                    if (lower.endsWith(ext)) return true;
                }
                return false;
            }
        });

        if (rawFiles == null) {
            files = new File[0];
            return;
        }

        // СОРТИРОВКА ПО ВОЗРАСТАНИЮ ДАТЫ ПОСЛЕДНЕГО ИЗМЕНЕНИЯ (старые → новые)
        Arrays.sort(rawFiles, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });

        files = rawFiles;
    }

    public void setFilter(File directory, String filterExtension) {
        reloadFiles(directory, filterExtension);
        currentFilter = filterExtension;
    }

    public int size() {
        return files.length;
    }

    public File getFile(int index) {
        if (index >= 0 && index < files.length) return files[index];
        return null;
    }

    @Override
    public Iterator getIterator() {
        return new ImageFileIterator();
    }

    private class ImageFileIterator implements Iterator {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return files.length > 0;
        }

        @Override
        public Image next() {
            if (files.length == 0) return null;
            currentIndex = (currentIndex + 1) % files.length;
            return loader.loadFromFile(files[currentIndex]);
        }

        @Override
        public boolean hasPrevious() {
            return files.length > 0;
        }

        @Override
        public Image previous() {
            if (files.length == 0) return null;
            currentIndex = (currentIndex - 1 + files.length) % files.length;
            return loader.loadFromFile(files[currentIndex]);
        }

        @Override
        public Image first() {
            if (files.length == 0) return null;
            currentIndex = 0;
            return loader.loadFromFile(files[currentIndex]);
        }

        @Override
        public Image last() {
            if (files.length == 0) return null;
            currentIndex = files.length - 1;
            return loader.loadFromFile(files[currentIndex]);
        }

        @Override
        public int getCurrentIndex() {
            return currentIndex;
        }

        @Override
        public File getCurrentFile() {
            if (files.length == 0) return null;
            return files[currentIndex];
        }
    }
}