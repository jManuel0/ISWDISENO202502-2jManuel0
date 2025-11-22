package com.voluntariado.plataforma.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {

    private String id;

    private String remitenteId;

    private String remitenteNombre;

    @NotBlank(message = "El destinatario es obligatorio")
    private String destinatarioId;

    private String destinatarioNombre;

    @NotBlank(message = "El asunto es obligatorio")
    private String asunto;

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    private boolean leido;

    private boolean archivado;

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaLectura;
}
