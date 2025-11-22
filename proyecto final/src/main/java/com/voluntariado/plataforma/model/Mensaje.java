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
@Document(collection = "mensajes")
public class Mensaje {

    @Id
    private String id;

    private String remitenteId;

    private String destinatarioId;

    private String asunto;

    private String contenido;

    @Builder.Default
    private boolean leido = false;

    @Builder.Default
    private boolean archivado = false;

    @Builder.Default
    private boolean eliminado = false;

    @CreatedDate
    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaLectura;
}
