package com.voluntariado.plataforma.dto.auth;

import com.voluntariado.plataforma.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String id;
    private String nombre;
    private String correo;
    private Rol rol;
    private String mensaje;
}
