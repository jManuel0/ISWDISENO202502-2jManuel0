package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.MensajeDTO;
import com.voluntariado.plataforma.security.CustomUserDetails;
import com.voluntariado.plataforma.service.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
@Tag(name = "Mensajes", description = "Sistema de mensajería interna")
@SecurityRequirement(name = "bearerAuth")
public class MensajeController {

    private final MensajeService mensajeService;

    @PostMapping
    @Operation(summary = "Enviar mensaje")
    public ResponseEntity<ApiResponse<MensajeDTO>> enviarMensaje(
            @Valid @RequestBody MensajeDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MensajeDTO mensaje = mensajeService.enviarMensaje(dto, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Mensaje enviado", mensaje));
    }

    @GetMapping("/bandeja-entrada")
    @Operation(summary = "Obtener bandeja de entrada")
    public ResponseEntity<ApiResponse<List<MensajeDTO>>> bandejaEntrada(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MensajeDTO> mensajes = mensajeService.obtenerBandejaSntrada(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(mensajes));
    }

    @GetMapping("/enviados")
    @Operation(summary = "Obtener mensajes enviados")
    public ResponseEntity<ApiResponse<List<MensajeDTO>>> mensajesEnviados(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MensajeDTO> mensajes = mensajeService.obtenerEnviados(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(mensajes));
    }

    @GetMapping("/no-leidos")
    @Operation(summary = "Obtener mensajes no leídos")
    public ResponseEntity<ApiResponse<List<MensajeDTO>>> noLeidos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MensajeDTO> mensajes = mensajeService.obtenerNoLeidos(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(mensajes));
    }

    @GetMapping("/contador")
    @Operation(summary = "Contar mensajes no leídos")
    public ResponseEntity<ApiResponse<Map<String, Long>>> contarNoLeidos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = mensajeService.contarNoLeidos(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("noLeidos", count)));
    }

    @GetMapping("/archivados")
    @Operation(summary = "Obtener mensajes archivados")
    public ResponseEntity<ApiResponse<List<MensajeDTO>>> archivados(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MensajeDTO> mensajes = mensajeService.obtenerArchivados(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(mensajes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Leer mensaje")
    public ResponseEntity<ApiResponse<MensajeDTO>> leerMensaje(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MensajeDTO mensaje = mensajeService.obtenerPorId(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(mensaje));
    }

    @PatchMapping("/{id}/archivar")
    @Operation(summary = "Archivar mensaje")
    public ResponseEntity<ApiResponse<Void>> archivarMensaje(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        mensajeService.archivarMensaje(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Mensaje archivado", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mensaje")
    public ResponseEntity<ApiResponse<Void>> eliminarMensaje(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        mensajeService.eliminarMensaje(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Mensaje eliminado", null));
    }
}
