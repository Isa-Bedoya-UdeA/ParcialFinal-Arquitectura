package com.udea.parcialfinal.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

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
 * Health check de la API. Útil para verificar que la app está arriba
 * y que la conexión a la base de datos responde.
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
                    + "una conexión a la base de datos configurada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio y DB operativos"),
            @ApiResponse(responseCode = "503", description = "Servicio arriba pero la DB no responde")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("servicio", "parcialFinal");
        body.put("estado", "UP");
        body.put("version", "v1");
        body.put("timestamp", LocalDateTime.now().toString());

        Map<String, Object> db = new LinkedHashMap<>();
        try (var conn = dataSource.getConnection()) {
            db.put("estado", "UP");
            db.put("url", conn.getMetaData().getURL());
            db.put("driver", conn.getMetaData().getDriverName());
        } catch (Exception e) {
            log.warn("Health check: no se pudo conectar a la base de datos", e);
            db.put("estado", "DOWN");
            db.put("error", e.getMessage());
            body.put("estado", "DEGRADED");
        }
        body.put("baseDeDatos", db);

        // Si la DB no responde, devolvemos 503 para que monitores
        // (Kubernetes, balanceadores) detecten el problema.
        if ("DOWN".equals(db.get("estado"))) {
            return ResponseEntity.status(503).body(body);
        }
        return ResponseEntity.ok(body);
    }
}
