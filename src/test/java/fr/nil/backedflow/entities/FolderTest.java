package fr.nil.backedflow.entities;

import fr.nil.backedflow.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FolderTest {

    private User user;
    @InjectMocks
    private Folder folder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        User user = User.builder().id(UUID.randomUUID()).firstName("test").lastName("test").build();

    }

    @Test
    void testGetId() {
        UUID id = UUID.randomUUID();
        folder.setId(id);
        assertEquals(id, folder.getId());
    }


    @Test
    void testGetFolderName() {
        String folderName = "Test Folder";
        folder.setFolderName(folderName);
        assertEquals(folderName, folder.getFolderName());
    }

    @Test
    void testGetFolderSize() {
        Long folderSize = 100L;
        folder.setFolderSize(folderSize);
        assertEquals(folderSize, folder.getFolderSize());
    }

    @Test
    void testGetFileCount() {
        int fileCount = 5;
        folder.setFileCount(fileCount);
        assertEquals(fileCount, folder.getFileCount());
    }

    @Test
    void testIsPrivate() {
        folder.setPrivate(true);
        assertTrue(folder.isPrivate());
    }

    @Test
    void testIsShared() {
        folder.setShared(true);
        assertTrue(folder.isShared());
    }

    @Test
    void testFolderOwner() {
        folder.setFolderOwner(user);
        assertEquals(user, folder.getFolderOwner());
    }

    @Test
    void testFolderEquals() {
        Folder folder1 = new Folder();
        folder1.setId(UUID.randomUUID());
        folder1.setFolderName("test");
        folder1.setFolderSize(100L);
        folder1.setFileCount(5);
        folder1.setPrivate(true);
        folder1.setShared(true);
        folder1.setFolderOwner(user);

        Folder folder2 = folder1;

        assertTrue(folder1.equals(folder2));
    }

}
