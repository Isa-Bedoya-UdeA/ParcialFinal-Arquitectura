package com.udea.parcialfinal.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Estructura estándar de error para todas las respuestas de fallo.
 * Se devuelve siempre en el mismo formato para que el cliente lo maneje fácil.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;
    private String path;
    /** Lista de errores de validación campo-por-campo (solo para 400 por @Valid). */
    private List<Map<String, String>> detalles;
}
