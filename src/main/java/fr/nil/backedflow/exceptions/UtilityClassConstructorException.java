package fr.nil.backedflow.exceptions;

public class UtilityClassConstructorException extends RuntimeException {

    public UtilityClassConstructorException() {
        super("Tried to construct an utility class");
    }
}
