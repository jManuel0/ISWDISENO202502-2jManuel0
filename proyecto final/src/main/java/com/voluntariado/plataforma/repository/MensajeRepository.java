package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.Mensaje;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends MongoRepository<Mensaje, String> {

    List<Mensaje> findByDestinatarioIdAndEliminadoFalse(String destinatarioId);

    List<Mensaje> findByRemitenteIdAndEliminadoFalse(String remitenteId);

    List<Mensaje> findByDestinatarioIdAndLeidoFalseAndEliminadoFalse(String destinatarioId);

    List<Mensaje> findByDestinatarioIdAndArchivadoTrueAndEliminadoFalse(String destinatarioId);

    long countByDestinatarioIdAndLeidoFalseAndEliminadoFalse(String destinatarioId);

    List<Mensaje> findByDestinatarioIdOrderByFechaEnvioDesc(String destinatarioId);
}
