package org.lboot.mrest.exception;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class MicroRestExceptionHandler {
    @ExceptionHandler(MicroRestException.class)
    public ResponseEntity<Object> handleMicroRequestException(MicroRestException e) {
        HttpStatus status = HttpStatus.resolve(e.code);
        if (status != null){
            return new ResponseEntity<>(e.getMessage(), status);
        }

        return new ResponseEntity<>(e.getMessage(),HttpStatus.resolve(e.code));
    }
}
