package com.sharememories.sharememories.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    @DisplayName("Given filename with extension " +
            "When generating new filename " +
            "Then proper string is returned")
    void generateProperNewFilenameByFilenameWithExtension() {
        String fileName = "originalFileName.png";

        String generatedFileName = FileUtils.generateUniqueName(fileName);

        String extension = generatedFileName.substring(generatedFileName.length() - 4);
        assertEquals(".png", extension);
    }

    @Test
    @DisplayName("Given filename without extension " +
            "When generating new filename " +
            "Then proper string is returned")
    void generateProperNewFilenameByFilenameWithoutExtension() {
        String fileName = "originalFileName";

        String generatedFileName = FileUtils.generateUniqueName(fileName);

        boolean extensionNotIncluded = (generatedFileName.lastIndexOf('.') == -1);
        assertTrue(extensionNotIncluded);
    }

    @Test
    @DisplayName("Given filename with extension " +
            "When generating new filename " +
            "Then returned string has proper length")
    void generateNewFilenameAndCheckLengthByFilenameWithExtension() {
        String fileName = "originalFileName.png";

        String generatedFileName = FileUtils.generateUniqueName(fileName);

        String nameWithoutExtension = generatedFileName.substring(0, generatedFileName.length() - 4);
        assertEquals(8, nameWithoutExtension.length());
    }

    @Test
    @DisplayName("Given filename without extension " +
            "When generating new filename " +
            "Then returned string has proper length")
    void generateNewFilenameAndCheckLengthByFilenameWithoutExtension() {
        String fileName = "originalFileName";

        String generatedFileName = FileUtils.generateUniqueName(fileName);

        assertEquals(8, generatedFileName.length());
    }
}