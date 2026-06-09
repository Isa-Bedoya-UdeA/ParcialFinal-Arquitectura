package com.udea.parcialfinal.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.udea.parcialfinal.controller.EstudianteController;
import com.udea.parcialfinal.dto.EstudianteDTO;

/**
 * Convierte un EstudianteDTO en un EntityModel<EstudianteDTO> con los
 * links HATEOAS que tiene sentido exponer:
 *   - self:    GET /api/v1/estudiantes/{cedula}
 *   - notas:   GET /api/v1/estudiantes/{cedula}/notas
 *   - list:    GET /api/v1/estudiantes
 */
@Component
public class EstudianteModelAssembler
        implements RepresentationModelAssembler<EstudianteDTO, EntityModel<EstudianteDTO>> {

    @Override
    public EntityModel<EstudianteDTO> toModel(EstudianteDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(EstudianteController.class).obtenerPorCedula(dto.getCedula())).withSelfRel(),
                linkTo(methodOn(EstudianteController.class).obtenerNotasPorCedula(dto.getCedula())).withRel("notas"),
                linkTo(methodOn(EstudianteController.class).listarTodos()).withRel("estudiantes")
        );
    }
}
