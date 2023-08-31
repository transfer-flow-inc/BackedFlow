package fr.nil.backedflow.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("The provided token is invalid.");
    }
}
