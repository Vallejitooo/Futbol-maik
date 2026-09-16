package com.pruebatecnica.futbol5.model;

import java.util.List;

public class EquipoTitularResponse {

    private boolean exito;
    private String mensaje;
    private List<PuntajeJugador> titulares;

    public EquipoTitularResponse() {
    }

    public EquipoTitularResponse(boolean exito, String mensaje, List<PuntajeJugador> titulares) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.titulares = titulares;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<PuntajeJugador> getTitulares() {
        return titulares;
    }

    public void setTitulares(List<PuntajeJugador> titulares) {
        this.titulares = titulares;
    }
}
