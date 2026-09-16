package com.pruebatecnica.futbol5.service;

import com.pruebatecnica.futbol5.model.EntrenamientoRequest;
import com.pruebatecnica.futbol5.model.EquipoTitularResponse;
import com.pruebatecnica.futbol5.model.PuntajeJugador;
import com.pruebatecnica.futbol5.model.ResultadoJugador;
import com.pruebatecnica.futbol5.repository.EntrenamientoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Contiene la lógica de negocio de la aplicación: aquí es donde se decide
 * "qué hacer" con los datos, a diferencia del Controller (que solo recibe
 * y responde peticiones HTTP) y del Repository (que solo guarda datos).
 *
 * @Service marca esta clase como un "bean" de Spring, para que pueda ser
 * inyectada automáticamente en el Controller.
 *
 * IMPORTANTE: a propósito, todo este servicio está escrito con bucles
 * "for" tradicionales, sin usar streams (.stream(), .map(), .filter())
 * ni expresiones lambda, para mantener el código más explícito y fácil
 * de leer paso a paso mientras se está aprendiendo.
 */
@Service
public class EntrenamientoService {

    // Pesos de cada métrica para calcular la "nota resultado", según la
    // prueba técnica: potencia 20%, velocidad 30%, pases 50%.
    private static final double PESO_POTENCIA = 0.20;
    private static final double PESO_VELOCIDAD = 0.30;
    private static final double PESO_PASES = 0.50;

    private final EntrenamientoRepository entrenamientoRepository;

    // "cantidadTitulares" es configurable desde application.properties con
    // la propiedad app.equipo.cantidad-titulares. Por defecto vale 5
    // (fútbol 5), pero así el mismo código sirve si el equipo escala a
    // fútbol 11 sin tener que tocar el código, solo la configuración.
    @Value("${app.equipo.cantidad-titulares:5}")
    private int cantidadTitulares;

    /**
     * Spring detecta que este constructor pide un EntrenamientoRepository
     * y, como esa clase también es un bean (@Repository), se lo entrega
     * automáticamente aquí. Esto se llama "inyección de dependencias por
     * constructor" y es la forma recomendada de hacerlo en Spring (mejor
     * que usar @Autowired directamente sobre el atributo).
     */
    public EntrenamientoService(EntrenamientoRepository entrenamientoRepository) {
        this.entrenamientoRepository = entrenamientoRepository;
    }

    /**
     * Registra los resultados de un entrenamiento completo.
     * Simplemente delega el guardado al repositorio.
     */
    public void registrarEntrenamiento(EntrenamientoRequest entrenamiento) {
        entrenamientoRepository.guardar(entrenamiento);
    }

    /**
     * Calcula y devuelve el equipo titular, siempre y cuando ya se hayan
     * registrado los 3 entrenamientos de la semana. Si falta alguno,
     * devuelve una respuesta indicando que no hay suficiente información.
     */
    public EquipoTitularResponse obtenerEquipoTitular() {

        // 1) Primero validamos la regla de negocio: solo se puede calcular
        //    el equipo titular si ya se registraron los 3 entrenamientos.
        if (!entrenamientoRepository.estanCompletosLosTresEntrenamientos()) {
            int registrados = entrenamientoRepository.contarEntrenamientosRegistrados();
            String mensaje = "No hay suficiente información para calcular el equipo titular. "
                    + "Se han registrado " + registrados + " de 3 entrenamientos de la semana.";
            return new EquipoTitularResponse(false, mensaje, new ArrayList<PuntajeJugador>());
        }

        // 2) Ya tenemos los 3 entrenamientos: calculamos el puntaje
        //    promedio de cada jugador a lo largo de los 3 entrenamientos.
        List<PuntajeJugador> puntajesPromedio = calcularPuntajesPromedioPorJugador();

        // 3) Ordenamos esa lista de mayor a menor puntaje, para poder
        //    tomar los primeros N jugadores (los titulares).
        ordenarDeMayorAMenorPuntaje(puntajesPromedio);

        // 4) Tomamos solo los primeros "cantidadTitulares" jugadores
        //    (5 por defecto, para fútbol 5).
        List<PuntajeJugador> titulares = tomarPrimerosN(puntajesPromedio, cantidadTitulares);

        String mensaje = "Equipo titular calculado correctamente con base en los 3 entrenamientos de la semana.";
        return new EquipoTitularResponse(true, mensaje, titulares);
    }

    /**
     * Recorre los 3 entrenamientos guardados y calcula, para cada
     * jugador, el promedio de su "nota resultado" a lo largo de esos 3
     * entrenamientos.
     *
     * Se usa un Map<String, Double> como acumulador de sumas y otro
     * Map<String, Integer> como contador de apariciones, para al final
     * dividir suma / cantidad y obtener el promedio de cada jugador,
     * incluso si algún jugador no participó en todos los entrenamientos.
     */
    private List<PuntajeJugador> calcularPuntajesPromedioPorJugador() {

        Map<String, Double> sumaNotasPorJugador = new java.util.LinkedHashMap<>();
        Map<String, Integer> cantidadNotasPorJugador = new java.util.LinkedHashMap<>();

        // obtenerTodos() nos da un Map<numeroEntrenamiento, EntrenamientoRequest>.
        Map<Integer, EntrenamientoRequest> todosLosEntrenamientos = entrenamientoRepository.obtenerTodos();

        // Recorremos cada entrenamiento registrado (1, 2 y 3).
        for (EntrenamientoRequest entrenamiento : todosLosEntrenamientos.values()) {

            // Dentro de cada entrenamiento, recorremos el resultado de
            // cada jugador con un for tradicional (sin streams).
            for (ResultadoJugador resultado : entrenamiento.getResultados()) {

                double notaDeEsteEntrenamiento = calcularNotaResultado(resultado);
                String nombreJugador = resultado.getJugador();

                if (sumaNotasPorJugador.containsKey(nombreJugador)) {
                    // Si el jugador ya tenía notas acumuladas, le sumamos esta nueva.
                    double sumaActual = sumaNotasPorJugador.get(nombreJugador);
                    sumaNotasPorJugador.put(nombreJugador, sumaActual + notaDeEsteEntrenamiento);

                    int cantidadActual = cantidadNotasPorJugador.get(nombreJugador);
                    cantidadNotasPorJugador.put(nombreJugador, cantidadActual + 1);
                } else {
                    // Si es la primera vez que vemos a este jugador, inicializamos sus contadores.
                    sumaNotasPorJugador.put(nombreJugador, notaDeEsteEntrenamiento);
                    cantidadNotasPorJugador.put(nombreJugador, 1);
                }
            }
        }

        // Con las sumas y cantidades ya listas, construimos la lista final
        // de PuntajeJugador calculando el promedio de cada uno.
        List<PuntajeJugador> resultado = new ArrayList<>();
        for (String nombreJugador : sumaNotasPorJugador.keySet()) {
            double suma = sumaNotasPorJugador.get(nombreJugador);
            int cantidad = cantidadNotasPorJugador.get(nombreJugador);
            double promedio = suma / cantidad;

            // Redondeamos a 2 decimales para que el resultado sea más legible.
            double promedioRedondeado = Math.round(promedio * 100.0) / 100.0;

            resultado.add(new PuntajeJugador(nombreJugador, promedioRedondeado));
        }

        return resultado;
    }

    /**
     * Aplica la fórmula indicada en la prueba técnica:
     *   nota resultado = potencia * 20% + velocidad * 30% + pases * 50%
     */
    private double calcularNotaResultado(ResultadoJugador resultado) {
        double aportePotencia = resultado.getPotenciaTiro() * PESO_POTENCIA;
        double aporteVelocidad = resultado.getVelocidad() * PESO_VELOCIDAD;
        double aportePases = resultado.getPasesEfectivos() * PESO_PASES;
        return aportePotencia + aporteVelocidad + aportePases;
    }

    /**
     * Ordena la lista de puntajes de mayor a menor.
     *
     * Usamos Collections.sort con una clase interna anónima que
     * implementa Comparator (en vez de una lambda), para no usar
     * programación funcional y que quede explícito qué hace la
     * comparación paso a paso.
     */
    private void ordenarDeMayorAMenorPuntaje(List<PuntajeJugador> puntajes) {
        Comparator<PuntajeJugador> comparadorDescendente = new Comparator<PuntajeJugador>() {
            @Override
            public int compare(PuntajeJugador jugador1, PuntajeJugador jugador2) {
                // Double.compare(a, b) devuelve negativo si a < b, 0 si son
                // iguales y positivo si a > b. Para ordenar de MAYOR a
                // MENOR invertimos el orden de los argumentos.
                return Double.compare(jugador2.getNotaResultado(), jugador1.getNotaResultado());
            }
        };

        java.util.Collections.sort(puntajes, comparadorDescendente);
    }

    /**
     * Devuelve una nueva lista con, como máximo, los primeros "cantidad"
     * elementos de la lista original (ya debe venir ordenada previamente).
     */
    private List<PuntajeJugador> tomarPrimerosN(List<PuntajeJugador> listaOrdenada, int cantidad) {
        List<PuntajeJugador> primerosN = new ArrayList<>();

        int limite = Math.min(cantidad, listaOrdenada.size());
        for (int i = 0; i < limite; i++) {
            primerosN.add(listaOrdenada.get(i));
        }

        return primerosN;
    }
}
