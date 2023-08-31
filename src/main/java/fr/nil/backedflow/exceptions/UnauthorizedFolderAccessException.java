package fr.nil.backedflow.exceptions;

public class UnauthorizedFolderAccessException extends RuntimeException {

    public UnauthorizedFolderAccessException() {
        super("You are not authorized to access this folder");
    }
}
