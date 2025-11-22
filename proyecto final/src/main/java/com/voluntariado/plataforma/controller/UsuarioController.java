package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.UsuarioDTO;
import com.voluntariado.plataforma.dto.auth.CambiarPasswordRequest;
import com.voluntariado.plataforma.model.enums.Rol;
import com.voluntariado.plataforma.security.CustomUserDetails;
import com.voluntariado.plataforma.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtenerPerfil(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar perfil del usuario autenticado")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarPerfil(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO usuario = usuarioService.actualizar(userDetails.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("Perfil actualizado", usuario));
    }

    @PostMapping("/cambiar-password")
    @Operation(summary = "Cambiar contraseña")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada", null));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar todos los usuarios (Admin)")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> listarTodos() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    @Operation(summary = "Listar usuarios por rol")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> listarPorRol(@PathVariable Rol rol) {
        List<UsuarioDTO> usuarios = usuarioService.listarPorRol(rol);
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtenerPorId(@PathVariable String id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cambiar rol de usuario (Admin)")
    public ResponseEntity<ApiResponse<Void>> cambiarRol(
            @PathVariable String id,
            @RequestParam Rol nuevoRol,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        usuarioService.cambiarRol(id, nuevoRol, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Rol actualizado", null));
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Activar/Desactivar usuario (Admin)")
    public ResponseEntity<ApiResponse<Void>> activarDesactivar(
            @PathVariable String id,
            @RequestParam boolean activo,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        usuarioService.activarDesactivar(id, activo, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(activo ? "Usuario activado" : "Usuario desactivado", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar usuario (Admin)")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        usuarioService.eliminar(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado", null));
    }
}
