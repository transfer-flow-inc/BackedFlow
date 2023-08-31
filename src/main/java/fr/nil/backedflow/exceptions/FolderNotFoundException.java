package fr.nil.backedflow.exceptions;

public class FolderNotFoundException extends RuntimeException{


    public FolderNotFoundException(String action) {
        super(action);
    }

    public FolderNotFoundException() {
        super("The requested folder can't be found");
    }
}

