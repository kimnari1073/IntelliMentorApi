package org.intelli.intellimentor.controller.advice;


import org.intelli.intellimentor.util.CustomJWTException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CustomControllerAdvice {

    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJWTException(CustomJWTException e){
        String msg = e.getMessage();

        return ResponseEntity.ok().body(Map.of("error",msg));
    }
}
