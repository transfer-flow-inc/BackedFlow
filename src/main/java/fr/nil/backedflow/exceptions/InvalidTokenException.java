package fr.nil.backedflow.exceptions;

public class InvalidTokenException extends Exception {

    public InvalidTokenException() {
        super("The provided token is invalid.");
    }
}
