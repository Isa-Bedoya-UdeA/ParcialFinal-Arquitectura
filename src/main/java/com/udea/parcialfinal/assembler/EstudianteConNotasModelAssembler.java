package com.udea.parcialfinal.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.udea.parcialfinal.controller.EstudianteController;
import com.udea.parcialfinal.controller.NotaController;
import com.udea.parcialfinal.dto.EstudianteConNotasDTO;

/**
 * EntityModel<EstudianteConNotasDTO> con links HATEOAS:
 *   - self:           GET /api/v1/estudiantes/{cedula}/notas
 *   - estudiante:     GET /api/v1/estudiantes/{cedula}
 *   - registrarNota:  POST /api/v1/notas
 *   - notas:          GET /api/v1/notas
 */
@Component
public class EstudianteConNotasModelAssembler
        implements RepresentationModelAssembler<EstudianteConNotasDTO, EntityModel<EstudianteConNotasDTO>> {

    @Override
    public EntityModel<EstudianteConNotasDTO> toModel(EstudianteConNotasDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(EstudianteController.class).obtenerNotasPorCedula(dto.getCedula())).withSelfRel(),
                linkTo(methodOn(EstudianteController.class).obtenerPorCedula(dto.getCedula())).withRel("estudiante"),
                linkTo(methodOn(NotaController.class).listarTodas()).withRel("notas"),
                linkTo(methodOn(NotaController.class).crearNota(null)).withRel("registrarNota")
        );
    }
}
