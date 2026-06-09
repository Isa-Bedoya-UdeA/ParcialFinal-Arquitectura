package com.udea.parcialfinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.udea.parcialfinal.model.Nota;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    /**
     * Devuelve todas las notas de un estudiante identificado por su cédula.
     * Spring Data genera la query JPQL automáticamente a partir del nombre del método.
     */
    List<Nota> findByEstudianteCedula(String cedula);

    /**
     * Busca una nota específica por la combinación estudiante + materia + periodo.
     * Se usa para evitar duplicados al registrar una nota nueva.
     */
    Optional<Nota> findByEstudianteCedulaAndMateriaCodigoAndPeriodo(
            String cedula, String codigoMateria, String periodo);

    /**
     * Devuelve el promedio global de todas las notas registradas.
     * Si no hay notas, JPQL AVG devuelve null → se traduce a Double null en Java.
     */
    @Query("SELECT AVG(n.valor) FROM Nota n")
    Double findPromedioValor();

    /**
     * Cuenta cuántas notas tienen valor >= umbral (usado para % de aprobados).
     */
    long countByValorGreaterThanEqual(Double valor);
}
