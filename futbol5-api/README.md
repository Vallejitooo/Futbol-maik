# Futbol5 API

API REST en **Spring Boot 3 + Java 17** para registrar entrenamientos de un
equipo de fútbol 5 y calcular, al final de la semana, el equipo titular.

## Requisitos

- Java 17 o superior
- Maven 3.8+ (o usar el `mvnw` incluido si lo agregas con `mvn -N io.takari:maven:wrapper`)

## Cómo ejecutar

```bash
mvn spring-boot:run
```

La API queda disponible en: `http://localhost:8080`

También puedes generar el `.jar` ejecutable y correrlo directamente:

```bash
mvn clean package
java -jar target/futbol5-api-1.0.0.jar
```

## Reglas de negocio implementadas

- Cada semana el equipo se entrena **3 veces**.
- Cada entrenamiento mide, por jugador: potencia de tiro (Km/h), velocidad
  (Km/h) y pases efectivos (#).
- La nota de cada entrenamiento se calcula así:
  `nota = potenciaTiro * 20% + velocidad * 30% + pasesEfectivos * 50%`
- El **equipo titular** se calcula con el **promedio** de la nota de cada
  jugador en los 3 entrenamientos de la semana, tomando los 5 jugadores
  (configurable) con mayor promedio.
- Si aún no se han registrado los 3 entrenamientos, el endpoint del
  equipo titular responde que no hay suficiente información (no lanza
  error, responde un JSON explicando la situación, con HTTP 409).
- La cantidad de titulares es configurable (`app.equipo.cantidad-titulares`
  en `application.properties`), pensando en que el equipo podría escalar
  a fútbol 11.

## Endpoints

### 1. Registrar un entrenamiento

```
POST /api/entrenamientos
Content-Type: application/json
```

Body de ejemplo:

```json
{
  "numeroEntrenamiento": 1,
  "resultados": [
    { "jugador": "Jugador1", "potenciaTiro": 10, "velocidad": 5, "pasesEfectivos": 25 },
    { "jugador": "Jugador2", "potenciaTiro": 16, "velocidad": 5, "pasesEfectivos": 20 },
    { "jugador": "Jugador3", "potenciaTiro": 15, "velocidad": 3, "pasesEfectivos": 30 },
    { "jugador": "Jugador4", "potenciaTiro": 12, "velocidad": 4, "pasesEfectivos": 18 },
    { "jugador": "Jugador5", "potenciaTiro": 11, "velocidad": 3, "pasesEfectivos": 19 },
    { "jugador": "Jugador6", "potenciaTiro": 9,  "velocidad": 3, "pasesEfectivos": 22 },
    { "jugador": "Jugador7", "potenciaTiro": 10, "velocidad": 2, "pasesEfectivos": 24 }
  ]
}
```

Repite la petición cambiando `numeroEntrenamiento` a `2` y a `3` (con los
resultados de esos días) para completar la semana.

Respuesta exitosa: `201 Created`

### 2. Obtener el equipo titular

```
GET /api/equipo-titular
```

- Si faltan entrenamientos por registrar, responde `409 Conflict`:

```json
{
  "exito": false,
  "mensaje": "No hay suficiente información para calcular el equipo titular. Se han registrado 1 de 3 entrenamientos de la semana.",
  "titulares": []
}
```

- Si ya se registraron los 3 entrenamientos, responde `200 OK`:

```json
{
  "exito": true,
  "mensaje": "Equipo titular calculado correctamente con base en los 3 entrenamientos de la semana.",
  "titulares": [
    { "jugador": "Jugador3", "notaResultado": 18.9 },
    { "jugador": "Jugador2", "notaResultado": 14.7 },
    { "jugador": "Jugador7", "notaResultado": 14.6 },
    { "jugador": "Jugador6", "notaResultado": 13.7 },
    { "jugador": "Jugador4", "notaResultado": 12.6 }
  ]
}
```

## Estructura del proyecto

```
src/main/java/com/pruebatecnica/futbol5/
├── Futbol5Application.java        # Clase principal (arranca la app)
├── controller/
│   └── EntrenamientoController.java   # Expone los 2 endpoints REST
├── service/
│   └── EntrenamientoService.java      # Lógica de negocio (cálculo de notas)
├── repository/
│   └── EntrenamientoRepository.java   # Almacenamiento en memoria
├── model/
│   ├── ResultadoJugador.java
│   ├── EntrenamientoRequest.java
│   ├── PuntajeJugador.java
│   └── EquipoTitularResponse.java
└── exception/
    └── ManejadorGlobalErrores.java    # Maneja errores de validación
```

## Notas de diseño / buenas prácticas aplicadas

- Separación en capas: **Controller -> Service -> Repository**.
- Inyección de dependencias por constructor (recomendado en Spring).
- Validación de datos de entrada con Bean Validation (`@Valid`, `@NotNull`,
  etc.) y manejo centralizado de errores con `@RestControllerAdvice`.
- Código escrito con bucles `for` tradicionales (sin streams ni
  expresiones lambda), a propósito, para fines de aprendizaje.
- Valor de "cantidad de titulares" externalizado en `application.properties`
  para que el sistema pueda escalar de fútbol 5 a fútbol 11 sin tocar código.

## Posibles mejoras a futuro

- Persistir los datos en una base de datos real (por ejemplo con Spring
  Data JPA + PostgreSQL) en lugar de guardarlos en memoria.
- Manejar varias semanas distintas (actualmente todo se acumula como "la
  semana actual"; se podría agregar un endpoint para reiniciar/cerrar
  semana, o identificar cada semana con una fecha).
- Agregar pruebas unitarias sobre `EntrenamientoService` cubriendo los
  distintos escenarios (semana incompleta, empates de puntaje, etc.).
