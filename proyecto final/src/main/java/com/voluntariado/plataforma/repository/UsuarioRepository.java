package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.Usuario;
import com.voluntariado.plataforma.model.enums.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByOrganizacionId(String organizacionId);

    Optional<Usuario> findByTokenVerificacion(String token);

    Optional<Usuario> findByTokenRecuperacion(String token);

    List<Usuario> findByNotificacionesActivasTrue();

    List<Usuario> findByRolAndActivoTrue(Rol rol);
}
