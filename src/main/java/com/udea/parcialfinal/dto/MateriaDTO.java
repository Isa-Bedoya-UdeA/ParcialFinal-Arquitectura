package com.udea.parcialfinal.dto;

import org.springframework.hateoas.server.core.Relation;

import com.udea.parcialfinal.model.Materia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de materia con todos sus campos. Distinto de MateriaSimpleDTO,
 * que es la versión "ligera" que viaja anidada dentro de NotaResponseDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Relation(itemRelation = "materia", collectionRelation = "materias")
public class MateriaDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private Integer creditos;

    public static MateriaDTO from(Materia m) {
        return MateriaDTO.builder()
                .id(m.getId())
                .codigo(m.getCodigo())
                .nombre(m.getNombre())
                .creditos(m.getCreditos())
                .build();
    }
}
