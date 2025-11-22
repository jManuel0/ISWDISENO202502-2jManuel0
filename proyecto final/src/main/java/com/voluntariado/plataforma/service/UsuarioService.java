package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.dto.UsuarioDTO;
import com.voluntariado.plataforma.dto.auth.CambiarPasswordRequest;
import com.voluntariado.plataforma.exception.BadRequestException;
import com.voluntariado.plataforma.exception.ResourceNotFoundException;
import com.voluntariado.plataforma.model.Usuario;
import com.voluntariado.plataforma.model.enums.Rol;
import com.voluntariado.plataforma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioDTO obtenerPorId(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return convertirADTO(usuario);
    }

    public UsuarioDTO obtenerPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "correo", correo));
        return convertirADTO(usuario);
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarActivos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO actualizar(String id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setNombre(dto.getNombre());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        usuario.setHabilidades(dto.getHabilidades());
        usuario.setAreasInteres(dto.getAreasInteres());
        usuario.setNotificacionesActivas(dto.isNotificacionesActivas());

        usuario = usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(id, usuario.getCorreo(),
                "ACTUALIZACION_PERFIL", "Usuario", id, "Actualización de datos personales");

        return convertirADTO(usuario);
    }

    public void cambiarPassword(String id, CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(id, usuario.getCorreo(),
                "CAMBIO_PASSWORD", "Usuario", id, "Cambio de contraseña");
    }

    public void cambiarRol(String id, Rol nuevoRol, String adminId) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        Rol rolAnterior = usuario.getRol();
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(adminId, null,
                "CAMBIO_ROL", "Usuario", id,
                String.format("Cambio de rol de %s a %s", rolAnterior, nuevoRol));
    }

    public void activarDesactivar(String id, boolean activo, String adminId) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setActivo(activo);
        usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(adminId, null,
                activo ? "ACTIVACION_USUARIO" : "DESACTIVACION_USUARIO",
                "Usuario", id,
                activo ? "Usuario activado" : "Usuario desactivado");
    }

    public void eliminar(String id, String adminId) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        auditoriaService.registrarAccion(adminId, null,
                "ELIMINACION_USUARIO", "Usuario", id,
                "Usuario eliminado: " + usuario.getCorreo());

        usuarioRepository.delete(usuario);
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .rol(usuario.getRol())
                .habilidades(usuario.getHabilidades())
                .areasInteres(usuario.getAreasInteres())
                .activo(usuario.isActivo())
                .verificado(usuario.isVerificado())
                .notificacionesActivas(usuario.isNotificacionesActivas())
                .organizacionId(usuario.getOrganizacionId())
                .fechaCreacion(usuario.getFechaCreacion())
                .ultimoAcceso(usuario.getUltimoAcceso())
                .build();
    }
}
