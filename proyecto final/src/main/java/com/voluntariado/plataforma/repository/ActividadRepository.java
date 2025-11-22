package com.voluntariado.plataforma.repository;

import com.voluntariado.plataforma.model.Actividad;
import com.voluntariado.plataforma.model.enums.EstadoActividad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActividadRepository extends MongoRepository<Actividad, String> {

    List<Actividad> findByEstado(EstadoActividad estado);

    List<Actividad> findByCoordinadorId(String coordinadorId);

    List<Actividad> findByOrganizacionId(String organizacionId);

    List<Actividad> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Actividad> findByFechaAfterAndEstado(LocalDateTime fecha, EstadoActividad estado);

    @Query("{ 'cuposDisponibles': { $gt: 0 }, 'estado': 'PROXIMA' }")
    List<Actividad> findActividadesDisponibles();

    List<Actividad> findByTituloContainingIgnoreCase(String titulo);

    List<Actividad> findByLugarContainingIgnoreCase(String lugar);

    @Query("{ 'fecha': { $gte: ?0, $lte: ?1 } }")
    List<Actividad> findByRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    List<Actividad> findByEstadoIn(List<EstadoActividad> estados);

    // Para recordatorios (actividades en las próximas 24 horas)
    @Query("{ 'fecha': { $gte: ?0, $lte: ?1 }, 'estado': 'PROXIMA' }")
    List<Actividad> findActividadesProximas(LocalDateTime inicio, LocalDateTime fin);
}
