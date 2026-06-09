package com.udea.parcialfinal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udea.parcialfinal.dto.MateriaDTO;
import com.udea.parcialfinal.exception.RecursoNoEncontradoException;
import com.udea.parcialfinal.model.Materia;
import com.udea.parcialfinal.repository.MateriaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio para Materias.
 * Hoy expone la lista completa y la consulta por código.
 */
@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepo;

    @Transactional(readOnly = true)
    public List<MateriaDTO> listarTodos() {
        return materiaRepo.findAll().stream()
                .map(MateriaDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MateriaDTO obtenerPorCodigo(String codigo) {
        Materia materia = materiaRepo.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una materia con el código " + codigo));
        return MateriaDTO.from(materia);
    }
}
