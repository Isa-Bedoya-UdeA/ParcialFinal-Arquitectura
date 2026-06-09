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

import com.udea.parcialfinal.assembler.MateriaModelAssembler;
import com.udea.parcialfinal.dto.MateriaDTO;
import com.udea.parcialfinal.service.MateriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de Materias con respuestas HATEOAS.
 * Versionado por URI: /api/v1/...
 */
@RestController
@RequestMapping("/api/v1/materias")
@RequiredArgsConstructor
@Tag(name = "Materias", description = "Catálogo de materias")
public class MateriaController {

    private final MateriaService materiaService;
    private final MateriaModelAssembler materiaAssembler;

    @Operation(
            summary = "Listar todas las materias",
            description = "Devuelve el catálogo de materias con links HATEOAS por cada una."
    )
    @ApiResponse(responseCode = "200", description = "Listado de materias")
    @GetMapping
    public CollectionModel<EntityModel<MateriaDTO>> listarTodos() {
        List<EntityModel<MateriaDTO>> materias = materiaService.listarTodos().stream()
                .map(materiaAssembler::toModel)
                .toList();
        return CollectionModel.of(materias,
                linkTo(methodOn(MateriaController.class).listarTodos()).withSelfRel());
    }

    @Operation(
            summary = "Obtener una materia por código",
            description = "Devuelve la materia correspondiente al código. 404 si no existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Materia encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una materia con ese código")
    })
    @GetMapping("/{codigo}")
    public EntityModel<MateriaDTO> obtenerPorCodigo(@PathVariable String codigo) {
        MateriaDTO dto = materiaService.obtenerPorCodigo(codigo);
        return materiaAssembler.toModel(dto);
    }
}
