package fr.nil.backedflow.exceptions;

public class FileDeletionException extends RuntimeException {

    public FileDeletionException() {
        super("An error occurred during the file deletion");
    }
}
