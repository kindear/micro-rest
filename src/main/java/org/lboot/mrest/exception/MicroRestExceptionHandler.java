package org.lboot.mrest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MicroRestExceptionHandler {
    @ExceptionHandler(MicroRestException.class)
    public ResponseEntity<String> handleMicroRequestException(MicroRestException e) {
        if (e.code == 404){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        else if (e.code == 401){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}