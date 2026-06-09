package com.udea.parcialfinal.dto;

import org.springframework.hateoas.server.core.Relation;

import com.udea.parcialfinal.model.Estudiante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO básico de estudiante. Usado en listados y en la respuesta de GET por cédula.
 * El nombre de la "relación" en respuestas CollectionModel se controla con
 * @Relation: el singular es "estudiante" y la colección "estudiantes".
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Relation(itemRelation = "estudiante", collectionRelation = "estudiantes")
public class EstudianteDTO {

    private Long id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String email;
    private String programa;

    public static EstudianteDTO from(Estudiante e) {
        return EstudianteDTO.builder()
                .id(e.getId())
                .cedula(e.getCedula())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .email(e.getEmail())
                .programa(e.getPrograma())
                .build();
    }
}
