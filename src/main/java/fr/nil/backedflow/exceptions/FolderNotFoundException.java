package fr.nil.backedflow.exceptions;

public class FolderNotFoundException extends RuntimeException{


    public FolderNotFoundException(String action) {
        super("Can't " + action + ", the request folder is not found.");
    }
}

