package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.dto.ActividadDTO;
import com.voluntariado.plataforma.exception.ResourceNotFoundException;
import com.voluntariado.plataforma.model.Actividad;
import com.voluntariado.plataforma.model.Usuario;
import com.voluntariado.plataforma.model.enums.EstadoActividad;
import com.voluntariado.plataforma.repository.ActividadRepository;
import com.voluntariado.plataforma.repository.InscripcionRepository;
import com.voluntariado.plataforma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final NotificacionService notificacionService;
    private final AuditoriaService auditoriaService;

    public ActividadDTO crear(ActividadDTO dto, String coordinadorId) {
        Actividad actividad = Actividad.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .fecha(dto.getFecha())
                .fechaFin(dto.getFechaFin())
                .lugar(dto.getLugar())
                .direccion(dto.getDireccion())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .cupos(dto.getCupos())
                .cuposDisponibles(dto.getCupos())
                .estado(EstadoActividad.PROXIMA)
                .coordinadorId(coordinadorId)
                .organizacionId(dto.getOrganizacionId())
                .requisitos(dto.getRequisitos())
                .categoriasActividad(dto.getCategoriasActividad())
                .horasVoluntariado(dto.getHorasVoluntariado())
                .build();

        actividad = actividadRepository.save(actividad);

        // Notificar a todos los voluntarios activos
        notificacionService.notificarNuevaActividad(actividad);

        auditoriaService.registrarAccion(coordinadorId, null,
                "CREACION_ACTIVIDAD", "Actividad", actividad.getId(),
                "Nueva actividad: " + actividad.getTitulo());

        return convertirADTO(actividad);
    }

    public ActividadDTO obtenerPorId(String id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));
        return convertirADTO(actividad);
    }

    public List<ActividadDTO> listarTodas() {
        return actividadRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ActividadDTO> listarDisponibles() {
        return actividadRepository.findActividadesDisponibles().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ActividadDTO> listarPorEstado(EstadoActividad estado) {
        return actividadRepository.findByEstado(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ActividadDTO> listarPorCoordinador(String coordinadorId) {
        return actividadRepository.findByCoordinadorId(coordinadorId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ActividadDTO> buscarPorTitulo(String titulo) {
        return actividadRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ActividadDTO> buscarPorLugar(String lugar) {
        return actividadRepository.findByLugarContainingIgnoreCase(lugar).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ActividadDTO> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return actividadRepository.findByRangoFechas(inicio, fin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ActividadDTO actualizar(String id, ActividadDTO dto, String usuarioId) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));

        actividad.setTitulo(dto.getTitulo());
        actividad.setDescripcion(dto.getDescripcion());
        actividad.setFecha(dto.getFecha());
        actividad.setFechaFin(dto.getFechaFin());
        actividad.setLugar(dto.getLugar());
        actividad.setDireccion(dto.getDireccion());
        actividad.setLatitud(dto.getLatitud());
        actividad.setLongitud(dto.getLongitud());
        actividad.setCupos(dto.getCupos());
        actividad.setRequisitos(dto.getRequisitos());
        actividad.setCategoriasActividad(dto.getCategoriasActividad());
        actividad.setHorasVoluntariado(dto.getHorasVoluntariado());

        if (dto.getEstado() != null) {
            actividad.setEstado(dto.getEstado());
        }

        actividad = actividadRepository.save(actividad);

        auditoriaService.registrarAccion(usuarioId, null,
                "ACTUALIZACION_ACTIVIDAD", "Actividad", id,
                "Actividad actualizada: " + actividad.getTitulo());

        return convertirADTO(actividad);
    }

    public void cambiarEstado(String id, EstadoActividad nuevoEstado, String usuarioId) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));

        actividad.setEstado(nuevoEstado);
        actividadRepository.save(actividad);

        auditoriaService.registrarAccion(usuarioId, null,
                "CAMBIO_ESTADO_ACTIVIDAD", "Actividad", id,
                "Nuevo estado: " + nuevoEstado);
    }

    public void eliminar(String id, String usuarioId) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));

        auditoriaService.registrarAccion(usuarioId, null,
                "ELIMINACION_ACTIVIDAD", "Actividad", id,
                "Actividad eliminada: " + actividad.getTitulo());

        actividadRepository.delete(actividad);
    }

    public void actualizarCuposDisponibles(String actividadId) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", actividadId));

        long inscritos = inscripcionRepository.countByActividadIdAndEstado(
                actividadId,
                com.voluntariado.plataforma.model.enums.EstadoInscripcion.APROBADA);

        actividad.setCuposDisponibles(actividad.getCupos() - (int) inscritos);
        actividadRepository.save(actividad);
    }

    private ActividadDTO convertirADTO(Actividad actividad) {
        String coordinadorNombre = null;
        if (actividad.getCoordinadorId() != null) {
            coordinadorNombre = usuarioRepository.findById(actividad.getCoordinadorId())
                    .map(Usuario::getNombre)
                    .orElse(null);
        }

        long inscritos = inscripcionRepository.countByActividadIdAndEstado(
                actividad.getId(),
                com.voluntariado.plataforma.model.enums.EstadoInscripcion.APROBADA);

        return ActividadDTO.builder()
                .id(actividad.getId())
                .titulo(actividad.getTitulo())
                .descripcion(actividad.getDescripcion())
                .fecha(actividad.getFecha())
                .fechaFin(actividad.getFechaFin())
                .lugar(actividad.getLugar())
                .direccion(actividad.getDireccion())
                .latitud(actividad.getLatitud())
                .longitud(actividad.getLongitud())
                .cupos(actividad.getCupos())
                .cuposDisponibles(actividad.getCuposDisponibles())
                .estado(actividad.getEstado())
                .coordinadorId(actividad.getCoordinadorId())
                .coordinadorNombre(coordinadorNombre)
                .organizacionId(actividad.getOrganizacionId())
                .requisitos(actividad.getRequisitos())
                .categoriasActividad(actividad.getCategoriasActividad())
                .horasVoluntariado(actividad.getHorasVoluntariado())
                .fechaCreacion(actividad.getFechaCreacion())
                .inscritos((int) inscritos)
                .build();
    }
}
