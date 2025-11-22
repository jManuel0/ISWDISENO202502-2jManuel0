package com.voluntariado.plataforma.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {

    // Estadísticas de voluntario individual
    private int totalActividades;
    private int totalHoras;
    private double promedioCalificacion;
    private int certificadosObtenidos;

    // Estadísticas globales (administrador)
    private long totalVoluntarios;
    private long voluntariosActivos;
    private long totalActividadesCreadas;
    private long actividadesActivas;
    private long actividadesFinalizadas;
    private long totalInscripciones;
    private long totalHorasAcumuladas;
}
