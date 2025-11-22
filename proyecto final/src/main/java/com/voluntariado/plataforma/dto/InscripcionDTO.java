package com.voluntariado.plataforma.dto;

import com.voluntariado.plataforma.model.enums.EstadoInscripcion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionDTO {

    private String id;
    private String usuarioId;
    private String usuarioNombre;
    private String usuarioCorreo;
    private String actividadId;
    private String actividadTitulo;
    private EstadoInscripcion estado;
    private boolean asistio;
    private LocalDateTime fechaAsistencia;
    private String comentarioCoordinador;
    private Integer calificacion;
    private String evaluacion;
    private LocalDateTime fechaInscripcion;
}
