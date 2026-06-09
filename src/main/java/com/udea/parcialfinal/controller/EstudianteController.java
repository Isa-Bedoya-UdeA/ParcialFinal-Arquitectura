package com.udea.parcialfinal.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.parcialfinal.assembler.EstudianteConNotasModelAssembler;
import com.udea.parcialfinal.assembler.EstudianteModelAssembler;
import com.udea.parcialfinal.dto.EstudianteConNotasDTO;
import com.udea.parcialfinal.dto.EstudianteDTO;
import com.udea.parcialfinal.service.EstudianteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de Estudiantes con respuestas HATEOAS.
 * Versionado por URI: /api/v1/...
 *
 * Cada respuesta incluye el bloque "_links" con relaciones navegables
 * (self, notas, estudiantes, etc.) según el recurso.
 */
@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Operaciones de consulta y navegación sobre estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final EstudianteModelAssembler estudianteAssembler;
    private final EstudianteConNotasModelAssembler estudianteConNotasAssembler;

    @Operation(
            summary = "Listar todos los estudiantes",
            description = "Devuelve la lista completa de estudiantes con links HATEOAS."
    )
    @ApiResponse(responseCode = "200", description = "Listado de estudiantes")
    @GetMapping
    public CollectionModel<EntityModel<EstudianteDTO>> listarTodos() {
        List<EntityModel<EstudianteDTO>> estudiantes = estudianteService.listarTodos().stream()
                .map(estudianteAssembler::toModel)
                .toList();
        return CollectionModel.of(estudiantes,
                linkTo(methodOn(EstudianteController.class).listarTodos()).withSelfRel());
    }

    @Operation(
            summary = "Obtener un estudiante por cédula",
            description = "Devuelve los datos de un estudiante. 404 si la cédula no existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un estudiante con esa cédula")
    })
    @GetMapping("/{cedula}")
    public EntityModel<EstudianteDTO> obtenerPorCedula(
            @Parameter(description = "Cédula del estudiante", example = "1234567890")
            @PathVariable String cedula) {
        EstudianteDTO dto = estudianteService.obtenerPorCedula(cedula);
        return estudianteAssembler.toModel(dto);
    }

    @Operation(
            summary = "Consultar las notas de un estudiante por su cédula",
            description = "Devuelve los datos básicos del estudiante y la lista completa de notas registradas. "
                    + "La respuesta incluye links HATEOAS: self, estudiante, notas y registrarNota."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante y notas encontradas"),
            @ApiResponse(responseCode = "404", description = "No existe un estudiante con esa cédula")
    })
    @GetMapping("/{cedula}/notas")
    public EntityModel<EstudianteConNotasDTO> obtenerNotasPorCedula(
            @Parameter(description = "Cédula del estudiante", example = "1234567890")
            @PathVariable String cedula) {
        EstudianteConNotasDTO dto = estudianteService.obtenerEstudianteConNotas(cedula);
        return estudianteConNotasAssembler.toModel(dto);
    }
}
