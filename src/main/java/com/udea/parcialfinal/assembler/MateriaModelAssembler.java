package com.udea.parcialfinal.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.udea.parcialfinal.controller.MateriaController;
import com.udea.parcialfinal.dto.MateriaDTO;

/**
 * Convierte un MateriaDTO en EntityModel<MateriaDTO> con los links:
 *   - self:  GET /api/v1/materias/{codigo}
 *   - list:  GET /api/v1/materias
 */
@Component
public class MateriaModelAssembler
        implements RepresentationModelAssembler<MateriaDTO, EntityModel<MateriaDTO>> {

    @Override
    public EntityModel<MateriaDTO> toModel(MateriaDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(MateriaController.class).obtenerPorCodigo(dto.getCodigo())).withSelfRel(),
                linkTo(methodOn(MateriaController.class).listarTodos()).withRel("materias")
        );
    }
}
