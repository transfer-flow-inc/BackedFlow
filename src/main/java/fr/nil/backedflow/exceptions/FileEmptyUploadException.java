package fr.nil.backedflow.exceptions;

public class FileEmptyUploadException extends RuntimeException {

    public FileEmptyUploadException() {
        super("Won't upload a empty file");
    }
}
