package com.udea.parcialfinal.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.udea.parcialfinal.controller.EstudianteController;
import com.udea.parcialfinal.controller.NotaController;
import com.udea.parcialfinal.dto.NotaResponseDTO;

/**
 * Convierte un NotaResponseDTO en EntityModel<NotaResponseDTO> con:
 *   - self:        GET /api/v1/notas/{id}
 *   - estudiante:  GET /api/v1/estudiantes/{cedula}/notas
 *   - notas:       GET /api/v1/notas
 */
@Component
public class NotaModelAssembler
        implements RepresentationModelAssembler<NotaResponseDTO, EntityModel<NotaResponseDTO>> {

    @Override
    public EntityModel<NotaResponseDTO> toModel(NotaResponseDTO dto) {
        // La cédula del estudiante no viene en NotaResponseDTO (solo el id).
        // Para construir el link "estudiante" usamos el id de la nota como contexto:
        // /api/v1/notas/{id}/estudiante. Si más adelante se expone la cédula en el DTO,
        // se puede refinar a /api/v1/estudiantes/{cedula}/notas.
        return EntityModel.of(dto,
                linkTo(methodOn(NotaController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(NotaController.class).listarTodas()).withRel("notas"),
                linkTo(methodOn(EstudianteController.class).obtenerNotasPorCedula("")).withRel("estudiantes")
        );
    }
}
