package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.dto.EvaluacionDTO;
import com.voluntariado.plataforma.dto.InscripcionDTO;
import com.voluntariado.plataforma.exception.BadRequestException;
import com.voluntariado.plataforma.exception.ResourceNotFoundException;
import com.voluntariado.plataforma.model.*;
import com.voluntariado.plataforma.model.enums.EstadoActividad;
import com.voluntariado.plataforma.model.enums.EstadoInscripcion;
import com.voluntariado.plataforma.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialVoluntariadoRepository historialRepository;
    private final NotificacionService notificacionService;
    private final ActividadService actividadService;
    private final AuditoriaService auditoriaService;

    public InscripcionDTO inscribirse(String usuarioId, String actividadId) {
        // Verificar que no esté ya inscrito
        if (inscripcionRepository.existsByUsuarioIdAndActividadId(usuarioId, actividadId)) {
            throw new BadRequestException("Ya estás inscrito en esta actividad");
        }

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", actividadId));

        // Verificar cupos disponibles
        if (actividad.getCuposDisponibles() <= 0) {
            throw new BadRequestException("No hay cupos disponibles para esta actividad");
        }

        // Verificar que la actividad esté próxima
        if (actividad.getEstado() != EstadoActividad.PROXIMA) {
            throw new BadRequestException("La actividad no está disponible para inscripciones");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .usuarioId(usuarioId)
                .actividadId(actividadId)
                .estado(EstadoInscripcion.PENDIENTE)
                .build();

        inscripcion = inscripcionRepository.save(inscripcion);

        // Notificar al usuario
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        notificacionService.notificarInscripcion(usuario, actividad, true);

        auditoriaService.registrarAccion(usuarioId, null,
                "INSCRIPCION", "Inscripcion", inscripcion.getId(),
                "Inscripción a actividad: " + actividad.getTitulo());

        return convertirADTO(inscripcion);
    }

    public void cancelarInscripcion(String usuarioId, String actividadId) {
        Inscripcion inscripcion = inscripcionRepository.findByUsuarioIdAndActividadId(usuarioId, actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", actividadId));

        // Verificar que falten al menos 24 horas
        if (actividad.getFecha().minusHours(24).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No puedes cancelar la inscripción con menos de 24 horas de anticipación");
        }

        inscripcion.setEstado(EstadoInscripcion.CANCELADA);
        inscripcionRepository.save(inscripcion);

        // Actualizar cupos disponibles
        actividadService.actualizarCuposDisponibles(actividadId);

        // Notificar
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        notificacionService.notificarInscripcion(usuario, actividad, false);

        auditoriaService.registrarAccion(usuarioId, null,
                "CANCELACION_INSCRIPCION", "Inscripcion", inscripcion.getId(),
                "Cancelación de inscripción a: " + actividad.getTitulo());
    }

    public void aprobarInscripcion(String inscripcionId, String coordinadorId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción", "id", inscripcionId));

        Actividad actividad = actividadRepository.findById(inscripcion.getActividadId())
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        if (actividad.getCuposDisponibles() <= 0) {
            throw new BadRequestException("No hay cupos disponibles");
        }

        inscripcion.setEstado(EstadoInscripcion.APROBADA);
        inscripcionRepository.save(inscripcion);

        // Actualizar cupos
        actividadService.actualizarCuposDisponibles(actividad.getId());

        // Verificar si se alcanzó el cupo máximo
        if (actividad.getCuposDisponibles() <= 1) {
            notificacionService.notificarCupoCompleto(actividad);
        }

        auditoriaService.registrarAccion(coordinadorId, null,
                "APROBACION_INSCRIPCION", "Inscripcion", inscripcionId, "Inscripción aprobada");
    }

    public void rechazarInscripcion(String inscripcionId, String coordinadorId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción", "id", inscripcionId));

        inscripcion.setEstado(EstadoInscripcion.RECHAZADA);
        inscripcionRepository.save(inscripcion);

        auditoriaService.registrarAccion(coordinadorId, null,
                "RECHAZO_INSCRIPCION", "Inscripcion", inscripcionId, "Inscripción rechazada");
    }

    public void registrarAsistencia(String inscripcionId, boolean asistio, String coordinadorId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción", "id", inscripcionId));

        inscripcion.setAsistio(asistio);
        inscripcion.setFechaAsistencia(LocalDateTime.now());
        inscripcionRepository.save(inscripcion);

        // Si asistió, crear registro en historial
        if (asistio) {
            Actividad actividad = actividadRepository.findById(inscripcion.getActividadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

            HistorialVoluntariado historial = HistorialVoluntariado.builder()
                    .usuarioId(inscripcion.getUsuarioId())
                    .actividadId(actividad.getId())
                    .tituloActividad(actividad.getTitulo())
                    .horasRealizadas(actividad.getHorasVoluntariado())
                    .fechaParticipacion(actividad.getFecha())
                    .build();

            historialRepository.save(historial);
        }

        auditoriaService.registrarAccion(coordinadorId, null,
                "REGISTRO_ASISTENCIA", "Inscripcion", inscripcionId,
                asistio ? "Asistencia confirmada" : "Inasistencia registrada");
    }

    public void evaluarVoluntario(String inscripcionId, EvaluacionDTO evaluacion, String coordinadorId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción", "id", inscripcionId));

        if (!inscripcion.isAsistio()) {
            throw new BadRequestException("No se puede evaluar a un voluntario que no asistió");
        }

        inscripcion.setCalificacion(evaluacion.getCalificacion());
        inscripcion.setEvaluacion(evaluacion.getComentario());
        inscripcionRepository.save(inscripcion);

        // Actualizar historial con la calificación
        List<HistorialVoluntariado> historiales = historialRepository.findByUsuarioId(inscripcion.getUsuarioId());
        historiales.stream()
                .filter(h -> h.getActividadId().equals(inscripcion.getActividadId()))
                .findFirst()
                .ifPresent(h -> {
                    h.setCalificacion(evaluacion.getCalificacion());
                    h.setEvaluacion(evaluacion.getComentario());
                    historialRepository.save(h);
                });

        auditoriaService.registrarAccion(coordinadorId, null,
                "EVALUACION_VOLUNTARIO", "Inscripcion", inscripcionId,
                "Calificación: " + evaluacion.getCalificacion());
    }

    public List<InscripcionDTO> listarPorUsuario(String usuarioId) {
        return inscripcionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<InscripcionDTO> listarPorActividad(String actividadId) {
        return inscripcionRepository.findByActividadId(actividadId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<InscripcionDTO> listarPorActividadYEstado(String actividadId, EstadoInscripcion estado) {
        return inscripcionRepository.findByActividadIdAndEstado(actividadId, estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private InscripcionDTO convertirADTO(Inscripcion inscripcion) {
        String usuarioNombre = null;
        String usuarioCorreo = null;
        String actividadTitulo = null;

        Usuario usuario = usuarioRepository.findById(inscripcion.getUsuarioId()).orElse(null);
        if (usuario != null) {
            usuarioNombre = usuario.getNombre();
            usuarioCorreo = usuario.getCorreo();
        }

        Actividad actividad = actividadRepository.findById(inscripcion.getActividadId()).orElse(null);
        if (actividad != null) {
            actividadTitulo = actividad.getTitulo();
        }

        return InscripcionDTO.builder()
                .id(inscripcion.getId())
                .usuarioId(inscripcion.getUsuarioId())
                .usuarioNombre(usuarioNombre)
                .usuarioCorreo(usuarioCorreo)
                .actividadId(inscripcion.getActividadId())
                .actividadTitulo(actividadTitulo)
                .estado(inscripcion.getEstado())
                .asistio(inscripcion.isAsistio())
                .fechaAsistencia(inscripcion.getFechaAsistencia())
                .comentarioCoordinador(inscripcion.getComentarioCoordinador())
                .calificacion(inscripcion.getCalificacion())
                .evaluacion(inscripcion.getEvaluacion())
                .fechaInscripcion(inscripcion.getFechaInscripcion())
                .build();
    }
}
