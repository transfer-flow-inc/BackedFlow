package fr.nil.backedflow.exceptions;

public class UnauthorizedFolderCreationException extends RuntimeException {
    public UnauthorizedFolderCreationException(String exceptionMessage) {
        super("Your account is not verified or you have surpassed your storage limit. (Exception: " + exceptionMessage + ")");
    }

    public UnauthorizedFolderCreationException() {
        super("Your account is not verified or you have surpassed your storage limit.");
    }
}
