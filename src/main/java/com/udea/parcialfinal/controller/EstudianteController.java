package com.udea.parcialfinal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.parcialfinal.dto.EstudianteConNotasDTO;
import com.udea.parcialfinal.service.EstudianteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de Estudiantes. Versionado por URI: /api/v1/...
 */
@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Operaciones de consulta sobre estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @Operation(
            summary = "Consultar las notas de un estudiante por su cédula",
            description = "Devuelve los datos básicos del estudiante y la lista completa de notas registradas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante y notas encontradas"),
            @ApiResponse(responseCode = "404", description = "No existe un estudiante con esa cédula")
    })
    @GetMapping("/{cedula}/notas")
    public ResponseEntity<EstudianteConNotasDTO> obtenerNotasPorCedula(
            @Parameter(description = "Cédula del estudiante", example = "1234567890")
            @PathVariable String cedula) {
        return ResponseEntity.ok(estudianteService.obtenerEstudianteConNotas(cedula));
    }
}
