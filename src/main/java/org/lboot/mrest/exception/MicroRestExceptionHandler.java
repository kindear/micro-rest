package org.lboot.mrest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MicroRestExceptionHandler {
    @ExceptionHandler(MicroRestException.class)
    public ResponseEntity<String> handleMicroRequestException(MicroRestException e) {
        HttpStatus status = HttpStatus.resolve(e.code);
        if (status != null){
            return new ResponseEntity<>(e.getMessage(), status);
        }
        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
