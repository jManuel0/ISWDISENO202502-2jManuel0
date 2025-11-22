package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.dto.NotificacionDTO;
import com.voluntariado.plataforma.exception.ResourceNotFoundException;
import com.voluntariado.plataforma.model.Actividad;
import com.voluntariado.plataforma.model.Notificacion;
import com.voluntariado.plataforma.model.Usuario;
import com.voluntariado.plataforma.model.enums.Rol;
import com.voluntariado.plataforma.model.enums.TipoNotificacion;
import com.voluntariado.plataforma.repository.InscripcionRepository;
import com.voluntariado.plataforma.repository.NotificacionRepository;
import com.voluntariado.plataforma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EmailService emailService;

    public void notificarNuevaActividad(Actividad actividad) {
        List<Usuario> voluntarios = usuarioRepository.findByRolAndActivoTrue(Rol.VOLUNTARIO);

        for (Usuario voluntario : voluntarios) {
            if (voluntario.isNotificacionesActivas()) {
                Notificacion notificacion = Notificacion.builder()
                        .usuarioId(voluntario.getId())
                        .titulo("Nueva actividad disponible")
                        .mensaje(String.format("Se ha creado una nueva actividad: %s. Fecha: %s. Lugar: %s",
                                actividad.getTitulo(),
                                actividad.getFecha().toString(),
                                actividad.getLugar()))
                        .tipo(TipoNotificacion.NUEVA_ACTIVIDAD)
                        .actividadId(actividad.getId())
                        .build();

                notificacionRepository.save(notificacion);

                // Enviar email
                emailService.enviarNotificacion(voluntario.getCorreo(),
                        "Nueva actividad: " + actividad.getTitulo(),
                        notificacion.getMensaje());
            }
        }
    }

    public void notificarInscripcion(Usuario usuario, Actividad actividad, boolean inscrito) {
        String titulo = inscrito ? "Inscripción confirmada" : "Cancelación de inscripción";
        String mensaje = inscrito
                ? String.format("Te has inscrito correctamente a la actividad: %s", actividad.getTitulo())
                : String.format("Has cancelado tu inscripción a la actividad: %s", actividad.getTitulo());

        Notificacion notificacion = Notificacion.builder()
                .usuarioId(usuario.getId())
                .titulo(titulo)
                .mensaje(mensaje)
                .tipo(TipoNotificacion.CONFIRMACION_INSCRIPCION)
                .actividadId(actividad.getId())
                .build();

        notificacionRepository.save(notificacion);

        emailService.enviarNotificacion(usuario.getCorreo(), titulo, mensaje);
    }

    public void notificarCupoCompleto(Actividad actividad) {
        // Notificar al coordinador
        Usuario coordinador = usuarioRepository.findById(actividad.getCoordinadorId()).orElse(null);

        if (coordinador != null) {
            Notificacion notificacion = Notificacion.builder()
                    .usuarioId(coordinador.getId())
                    .titulo("Cupo completo")
                    .mensaje(String.format("La actividad '%s' ha alcanzado su cupo máximo de participantes",
                            actividad.getTitulo()))
                    .tipo(TipoNotificacion.ALERTA_CUPO)
                    .actividadId(actividad.getId())
                    .build();

            notificacionRepository.save(notificacion);
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Todos los días a las 9:00 AM
    public void enviarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime manana = ahora.plusHours(24);

        // Buscar actividades que serán en las próximas 24 horas
        List<Actividad> actividadesProximas = new java.util.ArrayList<>();
        // Aquí se obtendría de ActividadRepository

        for (Actividad actividad : actividadesProximas) {
            // Obtener inscritos aprobados
            inscripcionRepository.findByActividadIdAndEstado(
                    actividad.getId(),
                    com.voluntariado.plataforma.model.enums.EstadoInscripcion.APROBADA
            ).forEach(inscripcion -> {
                Usuario usuario = usuarioRepository.findById(inscripcion.getUsuarioId()).orElse(null);
                if (usuario != null && usuario.isNotificacionesActivas()) {
                    Notificacion notificacion = Notificacion.builder()
                            .usuarioId(usuario.getId())
                            .titulo("Recordatorio de actividad")
                            .mensaje(String.format("Recuerda que mañana tienes la actividad: %s a las %s en %s",
                                    actividad.getTitulo(),
                                    actividad.getFecha().toLocalTime().toString(),
                                    actividad.getLugar()))
                            .tipo(TipoNotificacion.RECORDATORIO)
                            .actividadId(actividad.getId())
                            .build();

                    notificacionRepository.save(notificacion);

                    emailService.enviarNotificacion(usuario.getCorreo(),
                            "Recordatorio: " + actividad.getTitulo(),
                            notificacion.getMensaje());
                }
            });
        }
    }

    public void enviarComunicado(String titulo, String mensaje, List<String> destinatariosIds, String remitenteId) {
        for (String destinatarioId : destinatariosIds) {
            Usuario destinatario = usuarioRepository.findById(destinatarioId).orElse(null);
            if (destinatario != null) {
                Notificacion notificacion = Notificacion.builder()
                        .usuarioId(destinatarioId)
                        .titulo(titulo)
                        .mensaje(mensaje)
                        .tipo(TipoNotificacion.COMUNICADO)
                        .build();

                notificacionRepository.save(notificacion);

                if (destinatario.isNotificacionesActivas()) {
                    emailService.enviarNotificacion(destinatario.getCorreo(), titulo, mensaje);
                }
            }
        }
    }

    public List<NotificacionDTO> obtenerNotificacionesUsuario(String usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<NotificacionDTO> obtenerNoLeidas(String usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeidaFalse(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public long contarNoLeidas(String usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    public void marcarComoLeida(String notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación", "id", notificacionId));

        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    public void marcarTodasComoLeidas(String usuarioId) {
        List<Notificacion> noLeidas = notificacionRepository.findByUsuarioIdAndLeidaFalse(usuarioId);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
    }

    private NotificacionDTO convertirADTO(Notificacion notificacion) {
        return NotificacionDTO.builder()
                .id(notificacion.getId())
                .usuarioId(notificacion.getUsuarioId())
                .titulo(notificacion.getTitulo())
                .mensaje(notificacion.getMensaje())
                .tipo(notificacion.getTipo())
                .actividadId(notificacion.getActividadId())
                .leida(notificacion.isLeida())
                .fechaCreacion(notificacion.getFechaCreacion())
                .build();
    }
}
