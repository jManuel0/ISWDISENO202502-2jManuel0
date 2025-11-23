package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.dto.auth.AuthResponse;
import com.voluntariado.plataforma.dto.auth.LoginRequest;
import com.voluntariado.plataforma.dto.auth.RegistroRequest;
import com.voluntariado.plataforma.exception.BadRequestException;
import com.voluntariado.plataforma.exception.ResourceNotFoundException;
import com.voluntariado.plataforma.model.Usuario;
import com.voluntariado.plataforma.model.enums.Rol;
import com.voluntariado.plataforma.repository.UsuarioRepository;
import com.voluntariado.plataforma.security.CustomUserDetails;
import com.voluntariado.plataforma.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditoriaService auditoriaService;

    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new BadRequestException("El correo ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .habilidades(request.getHabilidades())
                .areasInteres(request.getAreasInteres())
                .rol(Rol.VOLUNTARIO)
                .activo(true)
                .verificado(false)
                .tokenVerificacion(UUID.randomUUID().toString())
                .notificacionesActivas(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(usuario.getId(), usuario.getCorreo(),
                "REGISTRO", "Usuario", usuario.getId(), "Nuevo registro de voluntario");

        CustomUserDetails userDetails = new CustomUserDetails(usuario);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .habilidades(usuario.getHabilidades())
                .rol(usuario.getRol())
                .mensaje("Registro exitoso")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "correo", request.getCorreo()));

        if (!usuario.isActivo()) {
            throw new BadRequestException("La cuenta está desactivada");
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(usuario.getId(), usuario.getCorreo(),
                "LOGIN", "Usuario", usuario.getId(), "Inicio de sesión");

        CustomUserDetails userDetails = new CustomUserDetails(usuario);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .habilidades(usuario.getHabilidades())
                .rol(usuario.getRol())
                .mensaje("Login exitoso")
                .build();
    }

    public void solicitarRecuperacionPassword(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "correo", correo));

        // Generar código de 6 dígitos
        String codigo = String.format("%06d", new java.util.Random().nextInt(1000000));
        usuario.setTokenRecuperacion(codigo);
        usuario.setTokenRecuperacionExpira(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        // TODO: Enviar email con código de recuperación
        // Por ahora el código se guarda en la BD y se puede verificar
    }

    public String verificarCodigoRecuperacion(String correo, String codigo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "correo", correo));

        if (usuario.getTokenRecuperacion() == null || !usuario.getTokenRecuperacion().equals(codigo)) {
            throw new BadRequestException("Código de verificación inválido");
        }

        if (usuario.getTokenRecuperacionExpira().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código ha expirado");
        }

        // Generar token para restablecer password
        String resetToken = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(resetToken);
        usuario.setTokenRecuperacionExpira(LocalDateTime.now().plusMinutes(30));
        usuarioRepository.save(usuario);

        return resetToken;
    }

    public void recuperarPassword(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(token)
                .orElseThrow(() -> new BadRequestException("Token de recuperación inválido"));

        if (usuario.getTokenRecuperacionExpira().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El token de recuperación ha expirado");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenRecuperacionExpira(null);
        usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(usuario.getId(), usuario.getCorreo(),
                "RECUPERACION_PASSWORD", "Usuario", usuario.getId(), "Contraseña recuperada");
    }
}
