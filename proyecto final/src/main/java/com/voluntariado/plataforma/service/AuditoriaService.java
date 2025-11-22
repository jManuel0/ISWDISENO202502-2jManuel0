package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.model.AuditoriaLog;
import com.voluntariado.plataforma.repository.AuditoriaLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaLogRepository auditoriaLogRepository;

    public void registrarAccion(String usuarioId, String correoUsuario, String accion,
                                String entidad, String entidadId, String detalles) {
        AuditoriaLog log = AuditoriaLog.builder()
                .usuarioId(usuarioId)
                .correoUsuario(correoUsuario)
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .detalles(detalles)
                .build();

        auditoriaLogRepository.save(log);
    }

    public List<AuditoriaLog> obtenerLogs() {
        return auditoriaLogRepository.findTop100ByOrderByFechaDesc();
    }

    public List<AuditoriaLog> obtenerLogsPorUsuario(String usuarioId) {
        return auditoriaLogRepository.findByUsuarioId(usuarioId);
    }

    public List<AuditoriaLog> obtenerLogsPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaLogRepository.findByFechaBetween(inicio, fin);
    }

    public List<AuditoriaLog> obtenerLogsPorAccion(String accion) {
        return auditoriaLogRepository.findByAccion(accion);
    }
}
