package fr.nil.backedflow.exceptions.handler;

import fr.nil.backedflow.exceptions.FileEmptyUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class FileEmptyUploadExceptionHandler {

    @ResponseBody
    @ExceptionHandler(FileEmptyUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String fileEmptyUploadException(FileEmptyUploadException e) {
        return e.getMessage();
    }
}