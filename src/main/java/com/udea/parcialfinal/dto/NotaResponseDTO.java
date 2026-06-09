package com.udea.parcialfinal.dto;

import java.time.LocalDate;

import com.udea.parcialfinal.model.Nota;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de salida para una nota. Se devuelve tanto en el POST (al crearla) como
 * embebido dentro de EstudianteConNotasDTO en el GET.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaResponseDTO {

    private Long id;
    private Double valor;
    private String periodo;
    private LocalDate fechaRegistro;
    private String observaciones;
    private MateriaSimpleDTO materia;

    public static NotaResponseDTO from(Nota n) {
        return NotaResponseDTO.builder()
                .id(n.getId())
                .valor(n.getValor())
                .periodo(n.getPeriodo())
                .fechaRegistro(n.getFechaRegistro())
                .observaciones(n.getObservaciones())
                .materia(MateriaSimpleDTO.from(n.getMateria()))
                .build();
    }
}
