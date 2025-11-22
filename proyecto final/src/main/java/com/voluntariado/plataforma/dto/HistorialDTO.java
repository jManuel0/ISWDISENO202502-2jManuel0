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
public class HistorialDTO {

    private String id;
    private String usuarioId;
    private String usuarioNombre;
    private String actividadId;
    private String tituloActividad;
    private int horasRealizadas;
    private LocalDateTime fechaParticipacion;
    private Integer calificacion;
    private String evaluacion;
    private String evidenciaUrl;
    private LocalDateTime fechaRegistro;
}
