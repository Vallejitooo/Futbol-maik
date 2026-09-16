package com.pruebatecnica.futbol5.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ManejadorGlobalErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> manejarErroresDeValidacion(MethodArgumentNotValidException excepcion) {

        List<String> mensajesDeError = new ArrayList<>();
        for (org.springframework.validation.FieldError error : excepcion.getBindingResult().getFieldErrors()) {
            String mensaje = error.getField() + ": " + error.getDefaultMessage();
            mensajesDeError.add(mensaje);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajesDeError);
    }
}
