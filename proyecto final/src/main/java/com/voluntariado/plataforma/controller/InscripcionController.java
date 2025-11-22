package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.EvaluacionDTO;
import com.voluntariado.plataforma.dto.InscripcionDTO;
import com.voluntariado.plataforma.model.enums.EstadoInscripcion;
import com.voluntariado.plataforma.security.CustomUserDetails;
import com.voluntariado.plataforma.service.InscripcionService;
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
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
@Tag(name = "Inscripciones", description = "Gestión de inscripciones a actividades")
@SecurityRequirement(name = "bearerAuth")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @PostMapping("/actividad/{actividadId}")
    @Operation(summary = "Inscribirse a una actividad")
    public ResponseEntity<ApiResponse<InscripcionDTO>> inscribirse(
            @PathVariable String actividadId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        InscripcionDTO inscripcion = inscripcionService.inscribirse(userDetails.getId(), actividadId);
        return ResponseEntity.ok(ApiResponse.success("Inscripción realizada exitosamente", inscripcion));
    }

    @DeleteMapping("/actividad/{actividadId}")
    @Operation(summary = "Cancelar inscripción a una actividad")
    public ResponseEntity<ApiResponse<Void>> cancelarInscripcion(
            @PathVariable String actividadId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        inscripcionService.cancelarInscripcion(userDetails.getId(), actividadId);
        return ResponseEntity.ok(ApiResponse.success("Inscripción cancelada", null));
    }

    @GetMapping("/mis-inscripciones")
    @Operation(summary = "Obtener mis inscripciones")
    public ResponseEntity<ApiResponse<List<InscripcionDTO>>> misInscripciones(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<InscripcionDTO> inscripciones = inscripcionService.listarPorUsuario(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(inscripciones));
    }

    @GetMapping("/actividad/{actividadId}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Listar inscripciones de una actividad (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<List<InscripcionDTO>>> listarPorActividad(@PathVariable String actividadId) {
        List<InscripcionDTO> inscripciones = inscripcionService.listarPorActividad(actividadId);
        return ResponseEntity.ok(ApiResponse.success(inscripciones));
    }

    @GetMapping("/actividad/{actividadId}/estado/{estado}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Listar inscripciones por actividad y estado")
    public ResponseEntity<ApiResponse<List<InscripcionDTO>>> listarPorActividadYEstado(
            @PathVariable String actividadId,
            @PathVariable EstadoInscripcion estado) {
        List<InscripcionDTO> inscripciones = inscripcionService.listarPorActividadYEstado(actividadId, estado);
        return ResponseEntity.ok(ApiResponse.success(inscripciones));
    }

    @PatchMapping("/{inscripcionId}/aprobar")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Aprobar inscripción (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<Void>> aprobarInscripcion(
            @PathVariable String inscripcionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        inscripcionService.aprobarInscripcion(inscripcionId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Inscripción aprobada", null));
    }

    @PatchMapping("/{inscripcionId}/rechazar")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Rechazar inscripción (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<Void>> rechazarInscripcion(
            @PathVariable String inscripcionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        inscripcionService.rechazarInscripcion(inscripcionId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Inscripción rechazada", null));
    }

    @PatchMapping("/{inscripcionId}/asistencia")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Registrar asistencia (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<Void>> registrarAsistencia(
            @PathVariable String inscripcionId,
            @RequestParam boolean asistio,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        inscripcionService.registrarAsistencia(inscripcionId, asistio, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(
                asistio ? "Asistencia registrada" : "Inasistencia registrada", null));
    }

    @PostMapping("/{inscripcionId}/evaluar")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @Operation(summary = "Evaluar participación del voluntario (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<Void>> evaluarVoluntario(
            @PathVariable String inscripcionId,
            @Valid @RequestBody EvaluacionDTO evaluacion,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        inscripcionService.evaluarVoluntario(inscripcionId, evaluacion, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Evaluación registrada", null));
    }
}
