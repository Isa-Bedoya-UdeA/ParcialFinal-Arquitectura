package com.udea.parcialfinal.exception;

/**
 * Se lanza cuando no se encuentra un recurso buscado por su clave natural
 * (cédula de estudiante, código de materia, etc.).
 * El GlobalExceptionHandler la traduce a HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
