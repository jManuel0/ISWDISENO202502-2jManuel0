package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.NotificacionDTO;
import com.voluntariado.plataforma.security.CustomUserDetails;
import com.voluntariado.plataforma.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Gestión de notificaciones")
@SecurityRequirement(name = "bearerAuth")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    @Operation(summary = "Obtener mis notificaciones")
    public ResponseEntity<ApiResponse<List<NotificacionDTO>>> misNotificaciones(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificacionDTO> notificaciones = notificacionService.obtenerNotificacionesUsuario(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(notificaciones));
    }

    @GetMapping("/no-leidas")
    @Operation(summary = "Obtener notificaciones no leídas")
    public ResponseEntity<ApiResponse<List<NotificacionDTO>>> noLeidas(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificacionDTO> notificaciones = notificacionService.obtenerNoLeidas(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(notificaciones));
    }

    @GetMapping("/contador")
    @Operation(summary = "Contar notificaciones no leídas")
    public ResponseEntity<ApiResponse<Map<String, Long>>> contarNoLeidas(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = notificacionService.contarNoLeidas(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("noLeidas", count)));
    }

    @PatchMapping("/{id}/leer")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<ApiResponse<Void>> marcarComoLeida(@PathVariable String id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok(ApiResponse.success("Notificación marcada como leída", null));
    }

    @PatchMapping("/leer-todas")
    @Operation(summary = "Marcar todas las notificaciones como leídas")
    public ResponseEntity<ApiResponse<Void>> marcarTodasComoLeidas(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificacionService.marcarTodasComoLeidas(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Todas las notificaciones marcadas como leídas", null));
    }

    @PostMapping("/comunicado")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Enviar comunicado a voluntarios (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<Void>> enviarComunicado(
            @RequestParam String titulo,
            @RequestParam String mensaje,
            @RequestBody List<String> destinatariosIds,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificacionService.enviarComunicado(titulo, mensaje, destinatariosIds, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Comunicado enviado", null));
    }
}
