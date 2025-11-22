package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.Organizacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizacionRepository extends MongoRepository<Organizacion, String> {

    Optional<Organizacion> findByNombre(String nombre);

    List<Organizacion> findByActivaTrue();

    boolean existsByNombre(String nombre);
}
