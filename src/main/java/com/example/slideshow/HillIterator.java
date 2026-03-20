package com.example.slideshow;

import java.io.File;
import java.util.List;

public class HillIterator implements Iterator {
    private List<File> files;
    private int currentIndex;
    private int step;
    private boolean increasing;

    public HillIterator(List<File> files) {
        this.files = files;
        this.currentIndex = 0;
        this.step = 1;
        this.increasing = true;
    }

    @Override
    public boolean hasNext() {
        return files != null && !files.isEmpty();
    }

    @Override
    public Object next() {
        if (!hasNext()) return null;

        File current = files.get(currentIndex);

        // вычисляем следующий индекс с переменным шагом
        int nextIndex = currentIndex + step;

        // проверка выхода за границы
        if (nextIndex >= files.size()) {
            // если вышли за правый край
            nextIndex = files.size() - 1;
            increasing = false;
            step = Math.max(1, step - 1);
            currentIndex = nextIndex;
        } else if (nextIndex < 0) {
            // если вышли за левый край
            nextIndex = 0;
            increasing = true;
            step = Math.min(3, step + 1);
            currentIndex = nextIndex;
        } else {
            currentIndex = nextIndex;

            // меняем шаг в зависимости от положения
            if (increasing) {
                step = Math.min(3, step + 1); // ускоряемся до 3
                // если дошли до середины - начинаем замедляться
                if (currentIndex >= files.size() / 2) {
                    increasing = false;
                }
            } else {
                step = Math.max(1, step - 1); // замедляемся до 1
            }
        }

        return current;
    }

    @Override
    public Object preview() {
        if (!hasNext()) return null;
        return files.get(currentIndex);
    }

    @Override
    public boolean hasPreview() {
        return hasNext();
    }

    public void reset() {
        currentIndex = 0;
        step = 1;
        increasing = true;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public File getCurrentFile() {
        if (!hasNext()) return null;
        return files.get(currentIndex);
    }
}