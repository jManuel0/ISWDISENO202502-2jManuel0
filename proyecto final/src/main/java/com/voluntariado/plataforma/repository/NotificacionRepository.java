package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.Notificacion;
import com.voluntariado.plataforma.model.enums.TipoNotificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends MongoRepository<Notificacion, String> {

    List<Notificacion> findByUsuarioId(String usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaFalse(String usuarioId);

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(String usuarioId);

    List<Notificacion> findByEnviadaFalse();

    List<Notificacion> findByTipo(TipoNotificacion tipo);

    List<Notificacion> findByActividadId(String actividadId);

    long countByUsuarioIdAndLeidaFalse(String usuarioId);
}
