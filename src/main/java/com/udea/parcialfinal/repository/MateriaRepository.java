package com.udea.parcialfinal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.parcialfinal.model.Materia;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    Optional<Materia> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
