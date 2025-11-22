package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.EstadisticasDTO;
import com.voluntariado.plataforma.dto.HistorialDTO;
import com.voluntariado.plataforma.security.CustomUserDetails;
import com.voluntariado.plataforma.service.HistorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
@Tag(name = "Historial", description = "Historial de participación de voluntarios")
@SecurityRequirement(name = "bearerAuth")
public class HistorialController {

    private final HistorialService historialService;

    @GetMapping
    @Operation(summary = "Obtener mi historial de participación")
    public ResponseEntity<ApiResponse<List<HistorialDTO>>> miHistorial(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<HistorialDTO> historial = historialService.obtenerHistorialUsuario(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(historial));
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener mis estadísticas de participación")
    public ResponseEntity<ApiResponse<EstadisticasDTO>> misEstadisticas(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EstadisticasDTO estadisticas = historialService.obtenerEstadisticasUsuario(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(estadisticas));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Obtener historial de un usuario específico (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<List<HistorialDTO>>> historialUsuario(@PathVariable String usuarioId) {
        List<HistorialDTO> historial = historialService.obtenerHistorialUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(historial));
    }

    @GetMapping("/usuario/{usuarioId}/estadisticas")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Obtener estadísticas de un usuario (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<EstadisticasDTO>> estadisticasUsuario(@PathVariable String usuarioId) {
        EstadisticasDTO estadisticas = historialService.obtenerEstadisticasUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(estadisticas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un registro de historial")
    public ResponseEntity<ApiResponse<HistorialDTO>> obtenerPorId(@PathVariable String id) {
        HistorialDTO historial = historialService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(historial));
    }
}
