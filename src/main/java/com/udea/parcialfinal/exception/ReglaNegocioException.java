package com.udea.parcialfinal.exception;

/**
 * Se lanza cuando una operación viola una regla de negocio
 * (por ejemplo: ya existe una nota para el mismo estudiante/materia/periodo).
 * El GlobalExceptionHandler la traduce a HTTP 400.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
