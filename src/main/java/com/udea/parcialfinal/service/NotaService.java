package com.udea.parcialfinal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udea.parcialfinal.dto.NotaRequestDTO;
import com.udea.parcialfinal.dto.NotaResponseDTO;
import com.udea.parcialfinal.exception.RecursoNoEncontradoException;
import com.udea.parcialfinal.exception.ReglaNegocioException;
import com.udea.parcialfinal.model.Estudiante;
import com.udea.parcialfinal.model.Materia;
import com.udea.parcialfinal.model.Nota;
import com.udea.parcialfinal.repository.EstudianteRepository;
import com.udea.parcialfinal.repository.MateriaRepository;
import com.udea.parcialfinal.repository.NotaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio para Notas.
 * Hoy expone el registro de una nota nueva a partir de la cédula y el código de materia.
 */
@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepo;
    private final EstudianteRepository estudianteRepo;
    private final MateriaRepository materiaRepo;

    /**
     * Crea una nueva nota validando que existan el estudiante y la materia.
     * El cliente envía cédula + código (claves naturales) en vez de ids internos,
     * por eso aquí se resuelven antes de persistir.
     */
    @Transactional
    public NotaResponseDTO crearNota(NotaRequestDTO req) {
        Estudiante estudiante = estudianteRepo.findByCedula(req.getCedulaEstudiante())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un estudiante con la cédula " + req.getCedulaEstudiante()));

        Materia materia = materiaRepo.findByCodigo(req.getCodigoMateria())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una materia con el código " + req.getCodigoMateria()));

        // Regla de negocio: no se permiten dos notas para el mismo
        // estudiante + materia + periodo. Si ya existe, fallamos con 400.
        notaRepo.findByEstudianteCedulaAndMateriaCodigoAndPeriodo(
                req.getCedulaEstudiante(), req.getCodigoMateria(), req.getPeriodo()
        ).ifPresent(n -> {
            throw new ReglaNegocioException(
                    "Ya existe una nota registrada para el estudiante "
                            + req.getCedulaEstudiante() + " en la materia "
                            + req.getCodigoMateria() + " para el periodo " + req.getPeriodo());
        });

        Nota nota = Nota.builder()
                .estudiante(estudiante)
                .materia(materia)
                .valor(req.getValor())
                .periodo(req.getPeriodo())
                .observaciones(req.getObservaciones())
                .build();

        Nota guardada = notaRepo.save(nota);
        return NotaResponseDTO.from(guardada);
    }
}
