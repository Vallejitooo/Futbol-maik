package com.pruebatecnica.futbol5.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class EntrenamientoRequest {

    @NotNull(message = "El número de entrenamiento es obligatorio")
    @Min(value = 1, message = "El número de entrenamiento debe ser 1, 2 o 3")
    @Max(value = 3, message = "El número de entrenamiento debe ser 1, 2 o 3")
    private Integer numeroEntrenamiento;

    @NotEmpty(message = "Debe incluir al menos un resultado de jugador")
    @Valid
    private List<ResultadoJugador> resultados;

    public EntrenamientoRequest() {
    }

    public Integer getNumeroEntrenamiento() {
        return numeroEntrenamiento;
    }

    public void setNumeroEntrenamiento(Integer numeroEntrenamiento) {
        this.numeroEntrenamiento = numeroEntrenamiento;
    }

    public List<ResultadoJugador> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoJugador> resultados) {
        this.resultados = resultados;
    }
}
