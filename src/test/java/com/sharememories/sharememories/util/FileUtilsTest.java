package com.sharememories.sharememories.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void Given_FileNameWithExtension_When_FileNameIsGenerated_Then_ProperStringIsReturned() {
        //Given
        String fileName = "originalFileName.png";
        //When
        String generatedFileName = FileUtils.generateUniqueName(fileName);
        //Then
        String extension = generatedFileName.substring(generatedFileName.length() - 4, generatedFileName.length());
        assertEquals(".png", extension);
    }

    @Test
    void Given_FileNameWithoutExtension_When_FileNameIsGenerated_Then_ProperStringIsReturned() {
        //Given
        String fileName = "originalFileName";
        //When
        String generatedFileName = FileUtils.generateUniqueName(fileName);
        //Then
        boolean extensionNotIncluded = (generatedFileName.lastIndexOf('.') == -1);
        assertTrue(extensionNotIncluded);
    }

    @Test
    void Given_FileNameWithExtension_When_FileNameIsGenerated_Then_ReturnedStringHasProperLength() {
        //Given
        String fileName = "originalFileName.png";
        //When
        String generatedFileName = FileUtils.generateUniqueName(fileName);
        //Then
        String nameWithoutExtension = generatedFileName.substring(0, generatedFileName.length() - 4);
        assertEquals(8, nameWithoutExtension.length());
    }

    @Test
    void Given_FileNameWithoutExtension_When_FileNameIsGenerated_Then_ReturnedStringHasProperLength() {
        //Given
        String fileName = "originalFileName";
        //Then
        String generatedFileName = FileUtils.generateUniqueName(fileName);
        //When
        assertEquals(8, generatedFileName.length());
    }
}