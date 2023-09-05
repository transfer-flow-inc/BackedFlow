package fr.nil.backedflow.exceptions;

public class StorageCalculationException extends RuntimeException {

    public StorageCalculationException(String message) {
        super("Something went wrong during the calculation of a storage : " + message);
    }
}
