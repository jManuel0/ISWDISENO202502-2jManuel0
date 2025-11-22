package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.CertificadoDTO;
import com.voluntariado.plataforma.security.CustomUserDetails;
import com.voluntariado.plataforma.service.CertificadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificados")
@RequiredArgsConstructor
@Tag(name = "Certificados", description = "Gestión de certificados de participación")
public class CertificadoController {

    private final CertificadoService certificadoService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener mis certificados")
    public ResponseEntity<ApiResponse<List<CertificadoDTO>>> misCertificados(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CertificadoDTO> certificados = certificadoService.listarPorUsuario(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(certificados));
    }

    @PostMapping("/generar")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Generar certificado para una actividad completada")
    public ResponseEntity<ApiResponse<CertificadoDTO>> generarCertificado(
            @RequestParam String actividadId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CertificadoDTO certificado = certificadoService.generarCertificado(userDetails.getId(), actividadId);
        return ResponseEntity.ok(ApiResponse.success("Certificado generado exitosamente", certificado));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener detalle de un certificado")
    public ResponseEntity<ApiResponse<CertificadoDTO>> obtenerPorId(@PathVariable String id) {
        CertificadoDTO certificado = certificadoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(certificado));
    }

    @GetMapping("/{id}/descargar")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Descargar certificado en PDF")
    public ResponseEntity<byte[]> descargarPDF(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        byte[] pdfContent = certificadoService.descargarCertificado(id, userDetails.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificado.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @PostMapping("/{id}/enviar-email")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Enviar certificado por email")
    public ResponseEntity<ApiResponse<Void>> enviarPorEmail(@PathVariable String id) {
        certificadoService.enviarCertificadoPorEmail(id);
        return ResponseEntity.ok(ApiResponse.success("Certificado enviado por email", null));
    }

    @GetMapping("/verificar/{codigo}")
    @Operation(summary = "Verificar autenticidad de un certificado (Público)")
    public ResponseEntity<ApiResponse<CertificadoDTO>> verificarCertificado(@PathVariable String codigo) {
        CertificadoDTO certificado = certificadoService.verificarCertificado(codigo);
        return ResponseEntity.ok(ApiResponse.success("Certificado válido", certificado));
    }

    @PostMapping("/generar-admin")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Generar certificado para un usuario (Coordinador/Admin)")
    public ResponseEntity<ApiResponse<CertificadoDTO>> generarCertificadoAdmin(
            @RequestParam String usuarioId,
            @RequestParam String actividadId) {
        CertificadoDTO certificado = certificadoService.generarCertificado(usuarioId, actividadId);
        return ResponseEntity.ok(ApiResponse.success("Certificado generado", certificado));
    }
}
