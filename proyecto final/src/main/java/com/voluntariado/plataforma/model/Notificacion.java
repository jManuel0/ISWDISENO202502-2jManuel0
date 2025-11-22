package com.voluntariado.plataforma.model;

import com.voluntariado.plataforma.model.enums.TipoNotificacion;
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
@Document(collection = "notificaciones")
public class Notificacion {

    @Id
    private String id;

    private String usuarioId;

    private String mensaje;

    private String titulo;

    private TipoNotificacion tipo;

    private String actividadId;

    @Builder.Default
    private boolean leida = false;

    @Builder.Default
    private boolean enviada = false;

    private LocalDateTime fechaEnvio;

    @CreatedDate
    private LocalDateTime fechaCreacion;
}
