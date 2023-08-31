package fr.nil.backedflow.exceptions;

public class FileUploadException extends RuntimeException {

    public FileUploadException() {
        super("Something went wrong during the file upload, please try again later");
    }
}
