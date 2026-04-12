package com.example.slideshow;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.io.File;

public class ExifReader {
    public static String getExifInfo(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            return "Нет данных EXIF";
        }
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            ExifSubIFDDirectory subDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            ExifIFD0Directory ifd0Dir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            StringBuilder sb = new StringBuilder();

            if (subDir != null) {
                // Дата съёмки
                if (subDir.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) {
                    sb.append("Дата съёмки: ").append(subDir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)).append("\n");
                }
                // Диафрагма – используем числовой код 0x829D, чтобы избежать проблем с именем константы
                if (subDir.containsTag(0x829D)) {
                    double fNumber = subDir.getDouble(0x829D);
                    sb.append("Диафрагма: f/").append(fNumber).append("\n");
                }
                // Выдержка
                if (subDir.containsTag(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)) {
                    sb.append("Выдержка: ").append(subDir.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)).append("\n");
                }
                // ISO
                if (subDir.containsTag(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)) {
                    sb.append("ISO: ").append(subDir.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)).append("\n");
                }
            }

            if (ifd0Dir != null) {
                if (ifd0Dir.containsTag(ExifIFD0Directory.TAG_MODEL)) {
                    sb.append("Камера: ").append(ifd0Dir.getString(ExifIFD0Directory.TAG_MODEL)).append("\n");
                }
            }

            return sb.length() == 0 ? "EXIF-информация отсутствует" : sb.toString();
        } catch (Exception e) {
            return "Ошибка чтения EXIF: " + e.getMessage();
        }
    }
}