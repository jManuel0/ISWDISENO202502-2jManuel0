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
@Document(collection = "evidencias")
public class Evidencia {

    @Id
    private String id;

    private String usuarioId;

    private String actividadId;

    private String inscripcionId;

    private String urlArchivo;

    private String tipoArchivo; // JPG, PNG, PDF

    private String descripcion;

    @Builder.Default
    private boolean aprobada = false;

    @CreatedDate
    private LocalDateTime fechaSubida;
}
