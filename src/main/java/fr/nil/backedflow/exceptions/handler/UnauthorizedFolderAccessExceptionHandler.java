package fr.nil.backedflow.exceptions.handler;

import fr.nil.backedflow.exceptions.UnauthorizedFolderAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class UnauthorizedFolderAccessExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UnauthorizedFolderAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unauthorizedFolderAccessException(UnauthorizedFolderAccessException e) {
        return e.getMessage();
    }
}