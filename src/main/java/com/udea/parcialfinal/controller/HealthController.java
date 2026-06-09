package com.udea.parcialfinal.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Health check de la API con respuesta HATEOAS.
 * Útil para verificar que la app está arriba y que la conexión a la base de datos responde.
 * Versionado por URI: /api/v1/...
 */
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Health", description = "Estado del servicio")
public class HealthController {

    private final DataSource dataSource;

    @Operation(
            summary = "Verificar estado del servicio y de la base de datos",
            description = "Devuelve el estado actual de la aplicación, la versión, "
                    + "el timestamp del servidor y un OK/ERROR según si logra abrir "
                    + "una conexión a la base de datos configurada. "
                    + "La respuesta incluye links HATEOAS para navegar al resto de la API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio y DB operativos"),
            @ApiResponse(responseCode = "503", description = "Servicio arriba pero la DB no responde")
    })
    @GetMapping
    public ResponseEntity<EntityModel<Map<String, Object>>> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("servicio", "parcialFinal");
        body.put("estado", "UP");
        body.put("version", "v1");
        body.put("timestamp", LocalDateTime.now().toString());

        Map<String, Object> db = new LinkedHashMap<>();
        HttpStatus statusFinal = HttpStatus.OK;
        try (var conn = dataSource.getConnection()) {
            db.put("estado", "UP");
            db.put("url", conn.getMetaData().getURL());
            db.put("driver", conn.getMetaData().getDriverName());
        } catch (Exception e) {
            log.warn("Health check: no se pudo conectar a la base de datos", e);
            db.put("estado", "DOWN");
            db.put("error", e.getMessage());
            body.put("estado", "DEGRADED");
            statusFinal = HttpStatus.SERVICE_UNAVAILABLE;
        }
        body.put("baseDeDatos", db);

        EntityModel<Map<String, Object>> model = EntityModel.of(body,
                linkTo(methodOn(HealthController.class).health()).withSelfRel(),
                linkTo(methodOn(EstudianteController.class).listarTodos()).withRel("estudiantes"),
                linkTo(methodOn(MateriaController.class).listarTodos()).withRel("materias"),
                linkTo(methodOn(NotaController.class).listarTodas()).withRel("notas"),
                linkTo(methodOn(DashboardController.class).obtenerEstadisticas()).withRel("dashboard")
        );

        return ResponseEntity.status(statusFinal).body(model);
    }
}
