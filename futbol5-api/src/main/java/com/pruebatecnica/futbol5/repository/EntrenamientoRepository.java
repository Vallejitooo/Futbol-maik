package com.pruebatecnica.futbol5.repository;

import com.pruebatecnica.futbol5.model.EntrenamientoRequest;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class EntrenamientoRepository {


    private final Map<Integer, EntrenamientoRequest> entrenamientosPorNumero =
            java.util.Collections.synchronizedMap(new HashMap<>());

    
    public void guardar(EntrenamientoRequest entrenamiento) {
        entrenamientosPorNumero.put(entrenamiento.getNumeroEntrenamiento(), entrenamiento);
    }

   
    public int contarEntrenamientosRegistrados() {
        return entrenamientosPorNumero.size();
    }

    
    public boolean estanCompletosLosTresEntrenamientos() {
        return entrenamientosPorNumero.containsKey(1)
                && entrenamientosPorNumero.containsKey(2)
                && entrenamientosPorNumero.containsKey(3);
    }

   
    public Map<Integer, EntrenamientoRequest> obtenerTodos() {
        return entrenamientosPorNumero;
    }

    
    public void limpiar() {
        entrenamientosPorNumero.clear();
    }
}
