package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.HistorialVoluntariado;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialVoluntariadoRepository extends MongoRepository<HistorialVoluntariado, String> {

    List<HistorialVoluntariado> findByUsuarioId(String usuarioId);

    List<HistorialVoluntariado> findByActividadId(String actividadId);

    List<HistorialVoluntariado> findByUsuarioIdOrderByFechaParticipacionDesc(String usuarioId);

    List<HistorialVoluntariado> findByFechaParticipacionBetween(LocalDateTime inicio, LocalDateTime fin);

    @Aggregation(pipeline = {
            "{ $match: { 'usuarioId': ?0 } }",
            "{ $group: { _id: null, totalHoras: { $sum: '$horasRealizadas' } } }"
    })
    Integer sumHorasByUsuarioId(String usuarioId);

    long countByUsuarioId(String usuarioId);
}
