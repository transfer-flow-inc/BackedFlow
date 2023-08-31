package fr.nil.backedflow.exceptions;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User cannot be found in the current repository.");
    }
}
