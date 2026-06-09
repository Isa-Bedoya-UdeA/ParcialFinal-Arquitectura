package com.udea.parcialfinal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.parcialfinal.dto.NotaRequestDTO;
import com.udea.parcialfinal.dto.NotaResponseDTO;
import com.udea.parcialfinal.service.NotaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de Notas. Versionado por URI: /api/v1/...
 */
@RestController
@RequestMapping("/api/v1/notas")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Registro y consulta de notas académicas")
public class NotaController {

    private final NotaService notaService;

    @Operation(
            summary = "Registrar una nueva nota",
            description = "Registra la nota de un estudiante en una materia y periodo específicos. "
                    + "El cliente envía cédula del estudiante y código de la materia, "
                    + "no ids internos. La nota debe estar entre 0.0 y 5.0."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nota creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio violada (ej. nota duplicada)"),
            @ApiResponse(responseCode = "404", description = "No existe el estudiante o la materia indicada")
    })
    @PostMapping
    public ResponseEntity<NotaResponseDTO> crearNota(@Valid @RequestBody NotaRequestDTO request) {
        NotaResponseDTO creada = notaService.crearNota(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
}
