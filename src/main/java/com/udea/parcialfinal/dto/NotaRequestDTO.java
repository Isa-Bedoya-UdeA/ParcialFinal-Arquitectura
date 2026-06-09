package com.udea.parcialfinal.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para el POST /api/notas.
 * El cliente envía la cédula y el código en vez de ids internos:
 * más amigable y más estable ante reasignación de ids.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaRequestDTO {

    @NotBlank(message = "La cédula del estudiante es obligatoria")
    private String cedulaEstudiante;

    @NotBlank(message = "El código de la materia es obligatorio")
    private String codigoMateria;

    @NotNull(message = "El valor de la nota es obligatorio")
    @DecimalMin(value = "0.0", message = "La nota mínima es 0.0")
    @DecimalMax(value = "5.0", message = "La nota máxima es 5.0")
    private Double valor;

    @NotBlank(message = "El periodo es obligatorio (ej. 2026-1)")
    private String periodo;

    private String observaciones;
}
