package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.EstadisticasDTO;
import com.voluntariado.plataforma.dto.InscripcionDTO;
import com.voluntariado.plataforma.dto.UsuarioDTO;
import com.voluntariado.plataforma.model.AuditoriaLog;
import com.voluntariado.plataforma.service.AuditoriaService;
import com.voluntariado.plataforma.service.EstadisticasService;
import com.voluntariado.plataforma.service.InscripcionService;
import com.voluntariado.plataforma.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
@Tag(name = "Administración", description = "Funcionalidades de administrador y coordinador")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final EstadisticasService estadisticasService;
    private final AuditoriaService auditoriaService;
    private final UsuarioService usuarioService;
    private final InscripcionService inscripcionService;

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas globales del sistema")
    public ResponseEntity<ApiResponse<EstadisticasDTO>> estadisticasGlobales() {
        EstadisticasDTO estadisticas = estadisticasService.obtenerEstadisticasGlobales();
        return ResponseEntity.ok(ApiResponse.success(estadisticas));
    }

    @GetMapping("/auditoria")
    @Operation(summary = "Obtener logs de auditoría")
    public ResponseEntity<ApiResponse<List<AuditoriaLog>>> obtenerLogs() {
        List<AuditoriaLog> logs = auditoriaService.obtenerLogs();
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/auditoria/usuario/{usuarioId}")
    @Operation(summary = "Obtener logs de un usuario específico")
    public ResponseEntity<ApiResponse<List<AuditoriaLog>>> logsPorUsuario(@PathVariable String usuarioId) {
        List<AuditoriaLog> logs = auditoriaService.obtenerLogsPorUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/auditoria/fecha")
    @Operation(summary = "Obtener logs por rango de fechas")
    public ResponseEntity<ApiResponse<List<AuditoriaLog>>> logsPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<AuditoriaLog> logs = auditoriaService.obtenerLogsPorFecha(inicio, fin);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/auditoria/accion/{accion}")
    @Operation(summary = "Obtener logs por tipo de acción")
    public ResponseEntity<ApiResponse<List<AuditoriaLog>>> logsPorAccion(@PathVariable String accion) {
        List<AuditoriaLog> logs = auditoriaService.obtenerLogsPorAccion(accion);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(ApiResponse.success(usuarios));
    }

    @GetMapping("/inscripciones")
    @Operation(summary = "Listar todas las inscripciones")
    public ResponseEntity<ApiResponse<List<InscripcionDTO>>> listarInscripciones() {
        List<InscripcionDTO> inscripciones = inscripcionService.listarTodas();
        return ResponseEntity.ok(ApiResponse.success(inscripciones));
    }
}
