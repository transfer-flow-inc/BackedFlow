package fr.nil.backedflow.exceptions.handler;

import fr.nil.backedflow.exceptions.FolderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class FolderNotFoundExceptionHandler {

    @ResponseBody
    @ExceptionHandler(FolderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String folderNotFoundException(FolderNotFoundException e) {
        return e.getMessage();
    }
}