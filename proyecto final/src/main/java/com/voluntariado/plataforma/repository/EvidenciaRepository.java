package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.Evidencia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends MongoRepository<Evidencia, String> {

    List<Evidencia> findByUsuarioId(String usuarioId);

    List<Evidencia> findByActividadId(String actividadId);

    List<Evidencia> findByInscripcionId(String inscripcionId);

    List<Evidencia> findByAprobadaFalse();

    List<Evidencia> findByUsuarioIdAndActividadId(String usuarioId, String actividadId);
}
