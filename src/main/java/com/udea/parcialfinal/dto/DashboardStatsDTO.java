package com.udea.parcialfinal.dto;

import org.springframework.hateoas.server.core.Relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO con las estadísticas que se muestran en el Dashboard del frontend.
 * Se calcula a partir de los repositorios de Estudiante, Materia y Nota.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Relation(itemRelation = "dashboardStats", collectionRelation = "dashboardStats")
public class DashboardStatsDTO {

    private long totalEstudiantes;
    private long totalMaterias;
    /** Promedio general de todas las notas registradas (0.0 si no hay notas). */
    private double promedioGeneral;
    /** Porcentaje de notas con valor >= 3.0 (0.0 si no hay notas). */
    private double porcentajeAprobados;
}
