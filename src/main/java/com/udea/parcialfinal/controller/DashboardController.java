package com.udea.parcialfinal.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.parcialfinal.dto.DashboardStatsDTO;
import com.udea.parcialfinal.dto.PromedioMateriaDTO;
import com.udea.parcialfinal.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints de estadísticas agregadas para el dashboard del frontend.
 * Las respuestas son HATEOAS con links a los listados de cada recurso.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Estadísticas agregadas para el panel principal")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Obtener estadísticas globales",
            description = "Devuelve los totales de estudiantes, materias, promedio general "
                    + "y porcentaje de notas aprobadas (>= 3.0)."
    )
    @GetMapping("/stats")
    public EntityModel<DashboardStatsDTO> obtenerEstadisticas() {
        DashboardStatsDTO stats = dashboardService.obtenerEstadisticas();
        return EntityModel.of(stats,
                linkTo(methodOn(DashboardController.class).obtenerEstadisticas()).withSelfRel(),
                linkTo(methodOn(DashboardController.class).obtenerPromediosPorMateria()).withRel("promediosPorMateria"),
                linkTo(methodOn(EstudianteController.class).listarTodos()).withRel("estudiantes"),
                linkTo(methodOn(MateriaController.class).listarTodos()).withRel("materias"),
                linkTo(methodOn(NotaController.class).listarTodas()).withRel("notas")
        );
    }

    @Operation(
            summary = "Obtener promedio de notas por materia",
            description = "Devuelve el promedio y la cantidad de notas agrupadas por cada materia."
    )
    @GetMapping("/promedios-por-materia")
    public CollectionModel<EntityModel<PromedioMateriaDTO>> obtenerPromediosPorMateria() {
        List<PromedioMateriaDTO> promedios = dashboardService.obtenerPromediosPorMateria();
        // Para el listado envolvemos cada elemento como EntityModel con link self.
        List<EntityModel<PromedioMateriaDTO>> models = promedios.stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(DashboardController.class).obtenerPromediosPorMateria()).withSelfRel()))
                .toList();
        return CollectionModel.of(models,
                linkTo(methodOn(DashboardController.class).obtenerPromediosPorMateria()).withSelfRel(),
                linkTo(methodOn(DashboardController.class).obtenerEstadisticas()).withRel("stats")
        );
    }
}
