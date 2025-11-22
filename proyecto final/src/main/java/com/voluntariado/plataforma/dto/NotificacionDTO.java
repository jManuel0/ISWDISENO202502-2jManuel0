package com.voluntariado.plataforma.dto;

import com.voluntariado.plataforma.model.enums.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {

    private String id;
    private String usuarioId;
    private String titulo;
    private String mensaje;
    private TipoNotificacion tipo;
    private String actividadId;
    private boolean leida;
    private LocalDateTime fechaCreacion;
}
