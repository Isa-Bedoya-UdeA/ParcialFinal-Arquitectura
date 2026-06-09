package com.udea.parcialfinal.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.parcialfinal.assembler.NotaModelAssembler;
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
 * Endpoints de Notas con respuestas HATEOAS.
 * Versionado por URI: /api/v1/...
 */
@RestController
@RequestMapping("/api/v1/notas")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Registro y consulta de notas académicas")
public class NotaController {

    private final NotaService notaService;
    private final NotaModelAssembler notaAssembler;

    @Operation(
            summary = "Listar todas las notas",
            description = "Devuelve la lista completa de notas registradas con links HATEOAS por cada una."
    )
    @ApiResponse(responseCode = "200", description = "Listado de notas")
    @GetMapping
    public CollectionModel<EntityModel<NotaResponseDTO>> listarTodas() {
        List<EntityModel<NotaResponseDTO>> notas = notaService.listarTodas().stream()
                .map(notaAssembler::toModel)
                .toList();
        return CollectionModel.of(notas,
                linkTo(methodOn(NotaController.class).listarTodas()).withSelfRel());
    }

    @Operation(
            summary = "Obtener una nota por id",
            description = "Devuelve la nota correspondiente al id. 404 si no existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una nota con ese id")
    })
    @GetMapping("/{id}")
    public EntityModel<NotaResponseDTO> obtenerPorId(@PathVariable Long id) {
        NotaResponseDTO dto = notaService.obtenerPorId(id);
        return notaAssembler.toModel(dto);
    }

    @Operation(
            summary = "Registrar una nueva nota",
            description = "Registra la nota de un estudiante en una materia y periodo específicos. "
                    + "El cliente envía cédula del estudiante y código de la materia, "
                    + "no ids internos. La nota debe estar entre 0.0 y 5.0. "
                    + "La respuesta es un EntityModel con links HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nota creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio violada (ej. nota duplicada)"),
            @ApiResponse(responseCode = "404", description = "No existe el estudiante o la materia indicada")
    })
    @PostMapping
    public ResponseEntity<EntityModel<NotaResponseDTO>> crearNota(@Valid @RequestBody NotaRequestDTO request) {
        NotaResponseDTO creada = notaService.crearNota(request);
        EntityModel<NotaResponseDTO> model = notaAssembler.toModel(creada);
        URI location = linkTo(methodOn(NotaController.class).obtenerPorId(creada.getId())).toUri();
        return ResponseEntity.created(location).body(model);
    }
}
