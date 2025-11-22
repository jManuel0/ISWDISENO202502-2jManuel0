package com.voluntariado.plataforma.model;

import com.voluntariado.plataforma.model.enums.EstadoActividad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "actividades")
public class Actividad {

    @Id
    private String id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime fecha;

    private LocalDateTime fechaFin;

    @NotBlank(message = "El lugar es obligatorio")
    private String lugar;

    private String direccion;

    private Double latitud;

    private Double longitud;

    @Min(value = 1, message = "Debe haber al menos 1 cupo")
    private int cupos;

    @Builder.Default
    private int cuposDisponibles = 0;

    @Builder.Default
    private EstadoActividad estado = EstadoActividad.PROXIMA;

    private String coordinadorId;

    private String organizacionId;

    @Builder.Default
    private List<String> requisitos = new ArrayList<>();

    @Builder.Default
    private List<String> categoriasActividad = new ArrayList<>();

    @Builder.Default
    private int horasVoluntariado = 0;

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
