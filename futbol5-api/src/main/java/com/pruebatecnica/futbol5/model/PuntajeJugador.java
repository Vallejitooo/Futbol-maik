package com.pruebatecnica.futbol5.model;

public class PuntajeJugador {

    private String jugador;
    private double notaResultado;

    public PuntajeJugador() {
    }

    public PuntajeJugador(String jugador, double notaResultado) {
        this.jugador = jugador;
        this.notaResultado = notaResultado;
    }

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }

    public double getNotaResultado() {
        return notaResultado;
    }

    public void setNotaResultado(double notaResultado) {
        this.notaResultado = notaResultado;
    }
}
