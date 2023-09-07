package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.services.folder.FolderService;
import fr.nil.backedflow.services.utils.FolderUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FolderServiceTest {

    //Generate unitary test for the Folder.service class using Junit 5 and Mocktio

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderService folderService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFolderByUrl() {
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getUrl()).thenReturn(FolderUtils.generateRandomURL());
        when(folderRepository.getFolderByUrl(any(String.class))).thenReturn(java.util.Optional.of(mockFolder));


        Folder result = folderRepository.getFolderByUrl(mockFolder.getUrl()).get();

        assertEquals(mockFolder, result);

    }

}
