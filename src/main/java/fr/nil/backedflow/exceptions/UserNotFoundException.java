package fr.nil.backedflow.exceptions;



public class UserNotFoundException extends Exception{
    public UserNotFoundException() {
        super("User cannot be found in the current repository.");
    }
}
