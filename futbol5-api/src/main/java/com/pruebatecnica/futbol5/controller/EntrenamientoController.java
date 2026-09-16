package com.pruebatecnica.futbol5.controller;

import com.pruebatecnica.futbol5.model.EntrenamientoRequest;
import com.pruebatecnica.futbol5.model.EquipoTitularResponse;
import com.pruebatecnica.futbol5.service.EntrenamientoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;

    public EntrenamientoController(EntrenamientoService entrenamientoService) {
        this.entrenamientoService = entrenamientoService;
    }

    @PostMapping("/entrenamientos")
    public ResponseEntity<String> registrarEntrenamiento(@Valid @RequestBody EntrenamientoRequest entrenamiento) {
        entrenamientoService.registrarEntrenamiento(entrenamiento);

        String mensaje = "Entrenamiento #" + entrenamiento.getNumeroEntrenamiento() + " registrado correctamente.";

        return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
    }

    @GetMapping("/equipo-titular")
    public ResponseEntity<EquipoTitularResponse> obtenerEquipoTitular() {
        EquipoTitularResponse respuesta = entrenamientoService.obtenerEquipoTitular();

        if (respuesta.isExito()) {

            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
        }
    }
}
