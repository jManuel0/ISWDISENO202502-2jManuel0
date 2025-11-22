package com.voluntariado.plataforma.dto;

import com.voluntariado.plataforma.model.enums.Rol;
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
public class UsuarioDTO {

    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private Rol rol;
    private List<String> habilidades;
    private List<String> areasInteres;
    private boolean activo;
    private boolean verificado;
    private boolean notificacionesActivas;
    private String organizacionId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
}
