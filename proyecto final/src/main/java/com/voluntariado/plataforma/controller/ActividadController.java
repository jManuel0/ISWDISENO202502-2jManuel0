package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ActividadDTO;
import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.model.enums.EstadoActividad;
import com.voluntariado.plataforma.security.CustomUserDetails;
import com.voluntariado.plataforma.service.ActividadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
@Tag(name = "Actividades", description = "Gestión de actividades de voluntariado")
public class ActividadController {

    private final ActividadService actividadService;

    @GetMapping
    @Operation(summary = "Listar todas las actividades")
    public ResponseEntity<ApiResponse<List<ActividadDTO>>> listarTodas() {
        List<ActividadDTO> actividades = actividadService.listarTodas();
        return ResponseEntity.ok(ApiResponse.success(actividades));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar actividades disponibles (con cupos)")
    public ResponseEntity<ApiResponse<List<ActividadDTO>>> listarDisponibles() {
        List<ActividadDTO> actividades = actividadService.listarDisponibles();
        return ResponseEntity.ok(ApiResponse.success(actividades));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener actividad por ID")
    public ResponseEntity<ApiResponse<ActividadDTO>> obtenerPorId(@PathVariable String id) {
        ActividadDTO actividad = actividadService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(actividad));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar actividades por estado")
    public ResponseEntity<ApiResponse<List<ActividadDTO>>> listarPorEstado(@PathVariable EstadoActividad estado) {
        List<ActividadDTO> actividades = actividadService.listarPorEstado(estado);
        return ResponseEntity.ok(ApiResponse.success(actividades));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar actividades por título o lugar")
    public ResponseEntity<ApiResponse<List<ActividadDTO>>> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String lugar) {
        List<ActividadDTO> actividades;
        if (titulo != null && !titulo.isEmpty()) {
            actividades = actividadService.buscarPorTitulo(titulo);
        } else if (lugar != null && !lugar.isEmpty()) {
            actividades = actividadService.buscarPorLugar(lugar);
        } else {
            actividades = actividadService.listarTodas();
        }
        return ResponseEntity.ok(ApiResponse.success(actividades));
    }

    @GetMapping("/fecha")
    @Operation(summary = "Listar actividades por rango de fechas")
    public ResponseEntity<ApiResponse<List<ActividadDTO>>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<ActividadDTO> actividades = actividadService.listarPorRangoFechas(inicio, fin);
        return ResponseEntity.ok(ApiResponse.success(actividades));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear nueva actividad (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<ActividadDTO>> crear(
            @Valid @RequestBody ActividadDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ActividadDTO actividad = actividadService.crear(dto, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Actividad creada exitosamente", actividad));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar actividad (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<ActividadDTO>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ActividadDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ActividadDTO actividad = actividadService.actualizar(id, dto, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Actividad actualizada", actividad));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cambiar estado de actividad")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @PathVariable String id,
            @RequestParam EstadoActividad estado,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        actividadService.cambiarEstado(id, estado, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar actividad (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        actividadService.eliminar(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Actividad eliminada", null));
    }

    @GetMapping("/mis-actividades")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar actividades del coordinador actual")
    public ResponseEntity<ApiResponse<List<ActividadDTO>>> misActividades(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ActividadDTO> actividades = actividadService.listarPorCoordinador(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(actividades));
    }
}
