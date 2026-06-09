package com.udea.parcialfinal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udea.parcialfinal.dto.DashboardStatsDTO;
import com.udea.parcialfinal.dto.PromedioMateriaDTO;
import com.udea.parcialfinal.model.Nota;
import com.udea.parcialfinal.repository.EstudianteRepository;
import com.udea.parcialfinal.repository.MateriaRepository;
import com.udea.parcialfinal.repository.NotaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio para el dashboard del frontend.
 * Calcula estadísticas agregadas a partir de los repositorios.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final double UMBRAL_APROBADO = 3.0;

    private final EstudianteRepository estudianteRepo;
    private final MateriaRepository materiaRepo;
    private final NotaRepository notaRepo;

    /**
     * Calcula las estadísticas globales: totales, promedio general y % de aprobados.
     */
    @Transactional(readOnly = true)
    public DashboardStatsDTO obtenerEstadisticas() {
        long totalEstudiantes = estudianteRepo.count();
        long totalMaterias = materiaRepo.count();
        long totalNotas = notaRepo.count();

        double promedioGeneral = 0.0;
        double porcentajeAprobados = 0.0;

        if (totalNotas > 0) {
            Double avg = notaRepo.findPromedioValor();
            promedioGeneral = avg == null ? 0.0 : round(avg, 2);

            long aprobadas = notaRepo.countByValorGreaterThanEqual(UMBRAL_APROBADO);
            porcentajeAprobados = round((aprobadas * 100.0) / totalNotas, 1);
        }

        return DashboardStatsDTO.builder()
                .totalEstudiantes(totalEstudiantes)
                .totalMaterias(totalMaterias)
                .promedioGeneral(promedioGeneral)
                .porcentajeAprobados(porcentajeAprobados)
                .build();
    }

    /**
     * Devuelve el promedio de notas agrupado por materia.
     * Útil para gráficos o tablas del dashboard.
     */
    @Transactional(readOnly = true)
    public List<PromedioMateriaDTO> obtenerPromediosPorMateria() {
        List<Nota> todas = notaRepo.findAll();
        return todas.stream()
                .collect(Collectors.groupingBy(
                        n -> n.getMateria().getCodigo(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    var materia = list.get(0).getMateria();
                                    double avg = list.stream()
                                            .mapToDouble(Nota::getValor)
                                            .average()
                                            .orElse(0.0);
                                    return PromedioMateriaDTO.builder()
                                            .codigoMateria(materia.getCodigo())
                                            .nombreMateria(materia.getNombre())
                                            .promedio(round(avg, 2))
                                            .cantidadNotas(list.size())
                                            .build();
                                }
                        )
                ))
                .values()
                .stream()
                .toList();
    }

    private static double round(double v, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(v * factor) / factor;
    }
}
