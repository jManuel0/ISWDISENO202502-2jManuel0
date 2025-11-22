package com.voluntariado.plataforma.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "historial_voluntariado")
public class HistorialVoluntariado {

    @Id
    private String id;

    private String usuarioId;

    private String actividadId;

    private String tituloActividad;

    private int horasRealizadas;

    private LocalDateTime fechaParticipacion;

    private Integer calificacion;

    private String evaluacion;

    private String evidenciaUrl;

    @CreatedDate
    private LocalDateTime fechaRegistro;
}
