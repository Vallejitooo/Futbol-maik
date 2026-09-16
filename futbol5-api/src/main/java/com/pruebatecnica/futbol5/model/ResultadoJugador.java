package com.pruebatecnica.futbol5.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ResultadoJugador {

    @NotBlank(message = "El nombre del jugador es obligatorio")
    private String jugador;
    @NotNull(message = "La potencia de tiro es obligatoria")
    @PositiveOrZero(message = "La potencia de tiro no puede ser negativa")
    private Double potenciaTiro; 

    @NotNull(message = "La velocidad es obligatoria")
    @PositiveOrZero(message = "La velocidad no puede ser negativa")
    private Double velocidad; // Km/h

    @NotNull(message = "Los pases efectivos son obligatorios")
    @PositiveOrZero(message = "Los pases efectivos no pueden ser negativos")
    private Integer pasesEfectivos; 

    public ResultadoJugador() {
    }

    public ResultadoJugador(String jugador, Double potenciaTiro, Double velocidad, Integer pasesEfectivos) {
        this.jugador = jugador;
        this.potenciaTiro = potenciaTiro;
        this.velocidad = velocidad;
        this.pasesEfectivos = pasesEfectivos;
    }

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }

    public Double getPotenciaTiro() {
        return potenciaTiro;
    }

    public void setPotenciaTiro(Double potenciaTiro) {
        this.potenciaTiro = potenciaTiro;
    }

    public Double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Double velocidad) {
        this.velocidad = velocidad;
    }

    public Integer getPasesEfectivos() {
        return pasesEfectivos;
    }

    public void setPasesEfectivos(Integer pasesEfectivos) {
        this.pasesEfectivos = pasesEfectivos;
    }
}
