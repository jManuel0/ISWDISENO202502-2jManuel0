package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.Certificado;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificadoRepository extends MongoRepository<Certificado, String> {

    List<Certificado> findByUsuarioId(String usuarioId);

    List<Certificado> findByActividadId(String actividadId);

    Optional<Certificado> findByUsuarioIdAndActividadId(String usuarioId, String actividadId);

    Optional<Certificado> findByCodigoVerificacion(String codigo);

    boolean existsByUsuarioIdAndActividadId(String usuarioId, String actividadId);
}
