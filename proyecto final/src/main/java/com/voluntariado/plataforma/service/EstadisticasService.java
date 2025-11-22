package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.dto.EstadisticasDTO;
import com.voluntariado.plataforma.model.enums.EstadoActividad;
import com.voluntariado.plataforma.model.enums.Rol;
import com.voluntariado.plataforma.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;
    private final InscripcionRepository inscripcionRepository;
    private final HistorialVoluntariadoRepository historialRepository;

    public EstadisticasDTO obtenerEstadisticasGlobales() {
        long totalVoluntarios = usuarioRepository.findByRol(Rol.VOLUNTARIO).size();
        long voluntariosActivos = usuarioRepository.findByRolAndActivoTrue(Rol.VOLUNTARIO).size();
        long totalActividades = actividadRepository.count();
        long actividadesActivas = actividadRepository.findByEstado(EstadoActividad.ACTIVA).size();
        long actividadesFinalizadas = actividadRepository.findByEstado(EstadoActividad.FINALIZADA).size();
        long totalInscripciones = inscripcionRepository.count();

        // Calcular total de horas
        long totalHoras = historialRepository.findAll().stream()
                .mapToInt(h -> h.getHorasRealizadas())
                .sum();

        return EstadisticasDTO.builder()
                .totalVoluntarios(totalVoluntarios)
                .voluntariosActivos(voluntariosActivos)
                .totalActividadesCreadas(totalActividades)
                .actividadesActivas(actividadesActivas)
                .actividadesFinalizadas(actividadesFinalizadas)
                .totalInscripciones(totalInscripciones)
                .totalHorasAcumuladas(totalHoras)
                .build();
    }
}
