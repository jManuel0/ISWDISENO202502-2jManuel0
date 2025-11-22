package com.voluntariado.plataforma.dto;

import com.voluntariado.plataforma.model.enums.EstadoActividad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActividadDTO {

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

    private int cuposDisponibles;

    private EstadoActividad estado;

    private String coordinadorId;

    private String coordinadorNombre;

    private String organizacionId;

    private List<String> requisitos;

    private List<String> categoriasActividad;

    private int horasVoluntariado;

    private LocalDateTime fechaCreacion;

    private int inscritos;
}
