package com.voluntariado.plataforma.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificadoDTO {

    private String id;
    private String usuarioId;
    private String actividadId;
    private String nombreVoluntario;
    private String tituloActividad;
    private String descripcion;
    private int horasParticipacion;
    private LocalDateTime fechaActividad;
    private LocalDateTime fechaEmision;
    private String codigoVerificacion;
    private String urlPdf;
}
