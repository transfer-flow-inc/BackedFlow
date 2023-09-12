package fr.nil.backedflow.manager;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StorageManagerTest {


    @InjectMocks
    private StorageManager storageManager;

    //@TempDir
    private Path tempDir;

    private UUID uuid = UUID.randomUUID();

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("junit5");

        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(storageManager, "storagePath", tempDir.toString());

    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void testGetTempStoragePath() {
        String expectedPath = tempDir + File.separator + "temp";
        String actualPath = storageManager.getTempStoragePath();

        assertEquals(expectedPath, actualPath);
    }

    @Test
    void testGetUserFileStoragePath() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(uuid);

        String expectedPath = tempDir + File.separator + "user_" + uuid;
        String actualPath = storageManager.getUserFileStoragePath(user);

        assertEquals(expectedPath, actualPath);
    }

    @Test
    void testGetUserFileStoragePathToFolder() {
        User user = mock(User.class);
        Folder folder = mock(Folder.class);
        UUID folderUUID = UUID.randomUUID();
        when(user.getId()).thenReturn(uuid);
        when(folder.getId()).thenReturn(folderUUID);

        String expectedPath = tempDir + File.separator + "user_" + uuid + File.separator + folderUUID;
        String actualPath = storageManager.getUserFileStoragePathToFolder(user, folder);

        assertEquals(expectedPath, actualPath);
    }


    @Test
    void testCheckStorageSize() throws IOException {
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.txt"));
        long expectedSize = Files.size(tempDir.resolve("file1.txt")) + Files.size(tempDir.resolve("file2.txt"));
        assertEquals(expectedSize, storageManager.checkStorageSize());
    }

    @Test
    void testCheckUserStorageSize() throws IOException {
        User user = mock(User.class);
        Folder folder = mock(Folder.class);
        UUID folderUUID = UUID.randomUUID();
        when(user.getId()).thenReturn(uuid);
        when(folder.getId()).thenReturn(folderUUID);
        Path userPath = tempDir.resolve("user_1");
        Files.createDirectory(userPath);
        Files.createFile(userPath.resolve("file1.txt"));
        Files.createFile(userPath.resolve("file2.txt"));
        long expectedSize = Files.size(userPath.resolve("file1.txt")) + Files.size(userPath.resolve("file2.txt"));
        assertEquals(expectedSize, storageManager.checkUserStorageSize(user));
    }

    @Test
    void testGetFormattedUserStorageSize() throws IOException {
        User user = mock(User.class);
        Folder folder = mock(Folder.class);
        UUID folderUUID = UUID.randomUUID();
        when(user.getId()).thenReturn(uuid);
        when(folder.getId()).thenReturn(folderUUID);
        Path userPath = tempDir.resolve("user_1");
        Files.createDirectory(userPath);
        Files.createFile(userPath.resolve("file1.txt"));
        Files.createFile(userPath.resolve("file2.txt"));
        long size = Files.size(userPath.resolve("file1.txt")) + Files.size(userPath.resolve("file2.txt"));
        float expectedSize = (float) size / (1024 * 1024 * 1024);
        assertEquals(expectedSize, storageManager.getFormattedUserStorageSize(user));
    }

    @Test
    void testCheckUserStorageSizeReturn() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(uuid);
        when(storageManager.getUserFileStoragePath(user)).thenReturn(null);
        assertEquals(0, storageManager.checkUserStorageSize(user));
    }
}