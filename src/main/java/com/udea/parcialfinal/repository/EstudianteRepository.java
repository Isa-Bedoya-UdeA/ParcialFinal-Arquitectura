package com.udea.parcialfinal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.parcialfinal.model.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByCedula(String cedula);

    boolean existsByCedula(String cedula);
}
