package com.voluntariado.plataforma.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.voluntariado.plataforma.dto.CertificadoDTO;
import com.voluntariado.plataforma.exception.BadRequestException;
import com.voluntariado.plataforma.exception.ResourceNotFoundException;
import com.voluntariado.plataforma.model.*;
import com.voluntariado.plataforma.model.enums.EstadoInscripcion;
import com.voluntariado.plataforma.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EmailService emailService;
    private final AuditoriaService auditoriaService;

    public CertificadoDTO generarCertificado(String usuarioId, String actividadId) {
        // Verificar que el voluntario asistió a la actividad
        Inscripcion inscripcion = inscripcionRepository.findByUsuarioIdAndActividadId(usuarioId, actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró inscripción para esta actividad"));

        if (!inscripcion.isAsistio()) {
            throw new BadRequestException("No se puede generar certificado: el voluntario no asistió a la actividad");
        }

        if (inscripcion.getEstado() != EstadoInscripcion.APROBADA) {
            throw new BadRequestException("La inscripción no está aprobada");
        }

        // Verificar si ya existe certificado
        if (certificadoRepository.existsByUsuarioIdAndActividadId(usuarioId, actividadId)) {
            throw new BadRequestException("Ya existe un certificado para esta actividad");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", actividadId));

        String codigoVerificacion = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Certificado certificado = Certificado.builder()
                .usuarioId(usuarioId)
                .actividadId(actividadId)
                .nombreVoluntario(usuario.getNombre())
                .tituloActividad(actividad.getTitulo())
                .descripcion(actividad.getDescripcion())
                .horasParticipacion(actividad.getHorasVoluntariado())
                .fechaActividad(actividad.getFecha())
                .fechaEmision(LocalDateTime.now())
                .codigoVerificacion(codigoVerificacion)
                .build();

        certificado = certificadoRepository.save(certificado);

        auditoriaService.registrarAccion(usuarioId, usuario.getCorreo(),
                "GENERACION_CERTIFICADO", "Certificado", certificado.getId(),
                "Certificado generado para: " + actividad.getTitulo());

        return convertirADTO(certificado);
    }

    public byte[] generarPDF(String certificadoId) {
        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado", "id", certificadoId));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());

            // Título
            Paragraph titulo = new Paragraph("CERTIFICADO DE PARTICIPACIÓN")
                    .setFontSize(28)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(titulo);

            document.add(new Paragraph("\n\n"));

            // Contenido
            Paragraph contenido = new Paragraph(
                    String.format("Se certifica que %s participó como voluntario/a en la actividad:",
                            certificado.getNombreVoluntario()))
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(contenido);

            document.add(new Paragraph("\n"));

            // Nombre de la actividad
            Paragraph actividad = new Paragraph(certificado.getTituloActividad())
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(actividad);

            document.add(new Paragraph("\n"));

            // Descripción
            Paragraph descripcion = new Paragraph(certificado.getDescripcion())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic();
            document.add(descripcion);

            document.add(new Paragraph("\n\n"));

            // Detalles
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
            Paragraph detalles = new Paragraph(
                    String.format("Fecha de la actividad: %s\nHoras de participación: %d horas",
                            certificado.getFechaActividad().format(formatter),
                            certificado.getHorasParticipacion()))
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(detalles);

            document.add(new Paragraph("\n\n\n"));

            // Código de verificación
            Paragraph codigo = new Paragraph(
                    String.format("Código de verificación: %s\nFecha de emisión: %s",
                            certificado.getCodigoVerificacion(),
                            certificado.getFechaEmision().format(formatter)))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY);
            document.add(codigo);

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del certificado", e);
        }
    }

    public byte[] descargarCertificado(String certificadoId, String usuarioId) {
        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado", "id", certificadoId));

        if (!certificado.getUsuarioId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para descargar este certificado");
        }

        return generarPDF(certificadoId);
    }

    public void enviarCertificadoPorEmail(String certificadoId) {
        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado", "id", certificadoId));

        Usuario usuario = usuarioRepository.findById(certificado.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        byte[] pdf = generarPDF(certificadoId);

        emailService.enviarCertificado(
                usuario.getCorreo(),
                usuario.getNombre(),
                certificado.getTituloActividad(),
                pdf);
    }

    public CertificadoDTO verificarCertificado(String codigoVerificacion) {
        Certificado certificado = certificadoRepository.findByCodigoVerificacion(codigoVerificacion)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado con el código proporcionado"));

        return convertirADTO(certificado);
    }

    public List<CertificadoDTO> listarPorUsuario(String usuarioId) {
        return certificadoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public CertificadoDTO obtenerPorId(String id) {
        Certificado certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado", "id", id));
        return convertirADTO(certificado);
    }

    private CertificadoDTO convertirADTO(Certificado certificado) {
        return CertificadoDTO.builder()
                .id(certificado.getId())
                .usuarioId(certificado.getUsuarioId())
                .actividadId(certificado.getActividadId())
                .nombreVoluntario(certificado.getNombreVoluntario())
                .tituloActividad(certificado.getTituloActividad())
                .descripcion(certificado.getDescripcion())
                .horasParticipacion(certificado.getHorasParticipacion())
                .fechaActividad(certificado.getFechaActividad())
                .fechaEmision(certificado.getFechaEmision())
                .codigoVerificacion(certificado.getCodigoVerificacion())
                .urlPdf(certificado.getUrlPdf())
                .build();
    }
}
