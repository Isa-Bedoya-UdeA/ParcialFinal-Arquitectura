package com.udea.parcialfinal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udea.parcialfinal.dto.EstudianteConNotasDTO;
import com.udea.parcialfinal.dto.NotaResponseDTO;
import com.udea.parcialfinal.exception.RecursoNoEncontradoException;
import com.udea.parcialfinal.model.Estudiante;
import com.udea.parcialfinal.model.Nota;
import com.udea.parcialfinal.repository.EstudianteRepository;
import com.udea.parcialfinal.repository.NotaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio para Estudiantes.
 * Hoy expone la consulta de notas por cédula que requiere el parcial.
 */
@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepo;
    private final NotaRepository notaRepo;

    /**
     * Devuelve el estudiante con todas sus notas registradas.
     * Lanza RecursoNoEncontradoException (→ 404) si la cédula no existe.
     */
    @Transactional(readOnly = true)
    public EstudianteConNotasDTO obtenerEstudianteConNotas(String cedula) {
        Estudiante estudiante = estudianteRepo.findByCedula(cedula)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un estudiante con la cédula " + cedula));

        List<Nota> notas = notaRepo.findByEstudianteCedula(cedula);
        List<NotaResponseDTO> notasDto = notas.stream()
                .map(NotaResponseDTO::from)
                .toList();

        return EstudianteConNotasDTO.of(estudiante, notasDto);
    }
}
