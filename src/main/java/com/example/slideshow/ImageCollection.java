package com.example.slideshow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class ImageCollection implements Aggregate {
    private File[] files;
    private File directory;
    private String currentFilter;
    private List<File> fileList;

    public ImageCollection(File directory) {
        this.directory = directory;
        this.currentFilter = "все";
        loadFiles();
    }

    // загрузка с фильтром
    public void setFilter(String filter) {
        this.currentFilter = filter;
        loadFiles();
    }

    private void loadFiles() {
        if (directory == null || !directory.exists()) {
            files = new File[0];
            fileList = Arrays.asList(files);
            return;
        }

        if (currentFilter.equals("все")) {
            // все картинки
            files = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String lower = name.toLowerCase();
                    return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                            lower.endsWith(".png") || lower.endsWith(".gif") ||
                            lower.endsWith(".bmp");
                }
            });
        } else {
            // только выбранный формат
            String ext = "." + currentFilter.toLowerCase();
            files = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(ext);
                }
            });
        }

        if (files == null) {
            files = new File[0];
        }
        fileList = Arrays.asList(files);
    }

    // обычный итератор
    public Iterator getIterator() {
        return new ImageFileIterator();
    }

    // новый итератор с горкой
    public Iterator getHillIterator() {
        return new HillIterator(fileList);
    }

    public File getFile(int index) {
        if (index >= 0 && index < files.length) {
            return files[index];
        }
        return null;
    }

    public int size() {
        return files.length;
    }

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

        public boolean hasPreview() {
            return files.length > 0;
        }

        public void reset() {
            currentIndex = 0;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public File getCurrentFile() {
            if (files.length == 0) return null;
            return files[currentIndex];
        }
    }
}