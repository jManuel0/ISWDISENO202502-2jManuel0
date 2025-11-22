package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.AuditoriaLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaLogRepository extends MongoRepository<AuditoriaLog, String> {

    List<AuditoriaLog> findByUsuarioId(String usuarioId);

    List<AuditoriaLog> findByAccion(String accion);

    List<AuditoriaLog> findByEntidad(String entidad);

    List<AuditoriaLog> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<AuditoriaLog> findTop100ByOrderByFechaDesc();
}
