package fr.nil.backedflow.services.utils;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @InjectMocks
    private FileUtils fileUtils = new FileUtils();

    @Test
    void getFileExtension() {
        File fileWithExt = new File("example.txt");
        File fileWithoutExt = new File("example");

        assertEquals("txt", fileUtils.getFileExtension(fileWithExt));
        assertEquals("", fileUtils.getFileExtension(fileWithoutExt));
    }

    @Test
    void isFileArchive() {
        File zip = new File("test.zip");
        File txt = new File("test.txt");

        assertTrue(fileUtils.isFileArchive(zip));
        assertFalse(fileUtils.isFileArchive(txt));
    }


}
