package fr.nil.backedflow.exceptions;

public class InvalidRequestException extends RuntimeException{


    public InvalidRequestException(String action) {
        super("Can't " + action + " the request is invalid !");
    }
}
