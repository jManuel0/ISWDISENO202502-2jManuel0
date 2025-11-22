package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.Inscripcion;
import com.voluntariado.plataforma.model.enums.EstadoInscripcion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends MongoRepository<Inscripcion, String> {

    List<Inscripcion> findByUsuarioId(String usuarioId);

    List<Inscripcion> findByActividadId(String actividadId);

    Optional<Inscripcion> findByUsuarioIdAndActividadId(String usuarioId, String actividadId);

    boolean existsByUsuarioIdAndActividadId(String usuarioId, String actividadId);

    List<Inscripcion> findByActividadIdAndEstado(String actividadId, EstadoInscripcion estado);

    List<Inscripcion> findByUsuarioIdAndEstado(String usuarioId, EstadoInscripcion estado);

    List<Inscripcion> findByActividadIdAndAsistioTrue(String actividadId);

    long countByActividadIdAndEstado(String actividadId, EstadoInscripcion estado);

    List<Inscripcion> findByUsuarioIdAndAsistioTrue(String usuarioId);
}
