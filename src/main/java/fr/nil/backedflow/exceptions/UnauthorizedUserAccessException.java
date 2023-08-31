package fr.nil.backedflow.exceptions;

public class UnauthorizedUserAccessException extends RuntimeException {


    public UnauthorizedUserAccessException(String message) {
        super(message);
    }
}
