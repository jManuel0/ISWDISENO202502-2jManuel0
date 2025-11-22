package com.voluntariado.plataforma.config;

import com.voluntariado.plataforma.model.Usuario;
import com.voluntariado.plataforma.model.enums.Rol;
import com.voluntariado.plataforma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Crear usuario administrador si no existe
        if (!usuarioRepository.existsByCorreo("admin@ong.org")) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .correo("admin@ong.org")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMINISTRADOR)
                    .activo(true)
                    .verificado(true)
                    .build();

            usuarioRepository.save(admin);
            log.info("Usuario administrador creado: admin@ong.org / admin123");
        }

        // Crear usuario coordinador de prueba si no existe
        if (!usuarioRepository.existsByCorreo("coordinador@ong.org")) {
            Usuario coordinador = Usuario.builder()
                    .nombre("Coordinador de Prueba")
                    .correo("coordinador@ong.org")
                    .password(passwordEncoder.encode("coord123"))
                    .rol(Rol.COORDINADOR)
                    .activo(true)
                    .verificado(true)
                    .build();

            usuarioRepository.save(coordinador);
            log.info("Usuario coordinador creado: coordinador@ong.org / coord123");
        }

        // Crear usuario voluntario de prueba si no existe
        if (!usuarioRepository.existsByCorreo("voluntario@test.com")) {
            Usuario voluntario = Usuario.builder()
                    .nombre("Voluntario de Prueba")
                    .correo("voluntario@test.com")
                    .password(passwordEncoder.encode("vol123"))
                    .rol(Rol.VOLUNTARIO)
                    .activo(true)
                    .verificado(true)
                    .telefono("3001234567")
                    .direccion("Calle 123 #45-67")
                    .build();

            usuarioRepository.save(voluntario);
            log.info("Usuario voluntario creado: voluntario@test.com / vol123");
        }

        log.info("===========================================");
        log.info("Inicialización de datos completada");
        log.info("===========================================");
        log.info("Usuarios de prueba:");
        log.info("  Admin: admin@ong.org / admin123");
        log.info("  Coordinador: coordinador@ong.org / coord123");
        log.info("  Voluntario: voluntario@test.com / vol123");
        log.info("===========================================");
    }
}
