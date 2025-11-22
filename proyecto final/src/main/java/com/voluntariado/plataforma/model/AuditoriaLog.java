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
@Document(collection = "auditoria_logs")
public class AuditoriaLog {

    @Id
    private String id;

    private String usuarioId;

    private String correoUsuario;

    private String accion;

    private String entidad;

    private String entidadId;

    private String detalles;

    private String ipAddress;

    @CreatedDate
    private LocalDateTime fecha;
}
