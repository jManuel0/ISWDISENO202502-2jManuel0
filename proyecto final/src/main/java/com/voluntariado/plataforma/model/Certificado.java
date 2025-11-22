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
@Document(collection = "certificados")
public class Certificado {

    @Id
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

    @CreatedDate
    private LocalDateTime fechaCreacion;
}
