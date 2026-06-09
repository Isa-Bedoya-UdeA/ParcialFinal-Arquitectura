package com.udea.parcialfinal.dto;

import org.springframework.hateoas.server.core.Relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO con el promedio de notas de una materia específica.
 * Devuelto en el endpoint de estadísticas por materia del dashboard.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Relation(itemRelation = "promedioMateria", collectionRelation = "promediosMaterias")
public class PromedioMateriaDTO {

    private String codigoMateria;
    private String nombreMateria;
    private double promedio;
    private long cantidadNotas;
}
