package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.dto.EstadisticasDTO;
import com.voluntariado.plataforma.dto.HistorialDTO;
import com.voluntariado.plataforma.exception.ResourceNotFoundException;
import com.voluntariado.plataforma.model.HistorialVoluntariado;
import com.voluntariado.plataforma.model.Usuario;
import com.voluntariado.plataforma.repository.CertificadoRepository;
import com.voluntariado.plataforma.repository.HistorialVoluntariadoRepository;
import com.voluntariado.plataforma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistorialService {

    private final HistorialVoluntariadoRepository historialRepository;
    private final UsuarioRepository usuarioRepository;
    private final CertificadoRepository certificadoRepository;

    public List<HistorialDTO> obtenerHistorialUsuario(String usuarioId) {
        return historialRepository.findByUsuarioIdOrderByFechaParticipacionDesc(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public EstadisticasDTO obtenerEstadisticasUsuario(String usuarioId) {
        List<HistorialVoluntariado> historiales = historialRepository.findByUsuarioId(usuarioId);

        int totalActividades = historiales.size();

        int totalHoras = historiales.stream()
                .mapToInt(HistorialVoluntariado::getHorasRealizadas)
                .sum();

        double promedioCalificacion = historiales.stream()
                .filter(h -> h.getCalificacion() != null)
                .mapToInt(HistorialVoluntariado::getCalificacion)
                .average()
                .orElse(0.0);

        long certificados = certificadoRepository.findByUsuarioId(usuarioId).size();

        return EstadisticasDTO.builder()
                .totalActividades(totalActividades)
                .totalHoras(totalHoras)
                .promedioCalificacion(promedioCalificacion)
                .certificadosObtenidos((int) certificados)
                .build();
    }

    public HistorialDTO obtenerPorId(String id) {
        HistorialVoluntariado historial = historialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historial", "id", id));
        return convertirADTO(historial);
    }

    public void actualizarEvidencia(String historialId, String evidenciaUrl) {
        HistorialVoluntariado historial = historialRepository.findById(historialId)
                .orElseThrow(() -> new ResourceNotFoundException("Historial", "id", historialId));

        historial.setEvidenciaUrl(evidenciaUrl);
        historialRepository.save(historial);
    }

    private HistorialDTO convertirADTO(HistorialVoluntariado historial) {
        String usuarioNombre = usuarioRepository.findById(historial.getUsuarioId())
                .map(Usuario::getNombre)
                .orElse(null);

        return HistorialDTO.builder()
                .id(historial.getId())
                .usuarioId(historial.getUsuarioId())
                .usuarioNombre(usuarioNombre)
                .actividadId(historial.getActividadId())
                .tituloActividad(historial.getTituloActividad())
                .horasRealizadas(historial.getHorasRealizadas())
                .fechaParticipacion(historial.getFechaParticipacion())
                .calificacion(historial.getCalificacion())
                .evaluacion(historial.getEvaluacion())
                .evidenciaUrl(historial.getEvidenciaUrl())
                .fechaRegistro(historial.getFechaRegistro())
                .build();
    }
}
