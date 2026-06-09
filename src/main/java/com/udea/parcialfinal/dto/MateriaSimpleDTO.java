package com.udea.parcialfinal.dto;

import com.udea.parcialfinal.model.Materia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO pequeño que viaja dentro de NotaResponseDTO.
 * No expone el id interno ni las notas, solo lo que el cliente necesita ver.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaSimpleDTO {

    private String codigo;
    private String nombre;
    private Integer creditos;

    public static MateriaSimpleDTO from(Materia m) {
        return MateriaSimpleDTO.builder()
                .codigo(m.getCodigo())
                .nombre(m.getNombre())
                .creditos(m.getCreditos())
                .build();
    }
}
