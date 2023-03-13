package com.sharememories.sharememories.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtils {

    public static String generateUniqueName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        int startIndex = (dotIndex == -1) ? fileName.length() : dotIndex;
        String fileExtension = fileName.substring(startIndex, fileName.length());

        return String.format("%s%s", RandomStringUtils.randomAlphanumeric(8), fileExtension);
    }

    public static void saveFile(String uploadDir, String fileName, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        InputStream inputStream = file.getInputStream();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void deleteFile(String deleteDir, String fileName) throws IOException{
        Path deletePath = Paths.get(deleteDir);

        if (Files.exists(deletePath)) {
            Path filePath = deletePath.resolve(fileName);
            Files.delete(filePath);
        }
    }
}
