package com.voluntariado.plataforma.model;

import com.voluntariado.plataforma.model.enums.EstadoInscripcion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inscripciones")
public class Inscripcion {

    @Id
    private String id;

    private String usuarioId;

    private String actividadId;

    @Builder.Default
    private EstadoInscripcion estado = EstadoInscripcion.PENDIENTE;

    @Builder.Default
    private boolean asistio = false;

    private LocalDateTime fechaAsistencia;

    private String comentarioCoordinador;

    private Integer calificacion; // 1-5

    private String evaluacion;

    @CreatedDate
    private LocalDateTime fechaInscripcion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
