package com.udea.parcialfinal.dto;

import java.util.List;

import com.udea.parcialfinal.model.Estudiante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de salida para GET /api/estudiantes/{cedula}/notas.
 * Devuelve los datos básicos del estudiante y todas sus notas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteConNotasDTO {

    private String cedula;
    private String nombreCompleto;
    private String programa;
    private List<NotaResponseDTO> notas;

    public static EstudianteConNotasDTO of(Estudiante e, List<NotaResponseDTO> notas) {
        return EstudianteConNotasDTO.builder()
                .cedula(e.getCedula())
                .nombreCompleto(e.getNombre() + " " + e.getApellido())
                .programa(e.getPrograma())
                .notas(notas)
                .build();
    }
}
