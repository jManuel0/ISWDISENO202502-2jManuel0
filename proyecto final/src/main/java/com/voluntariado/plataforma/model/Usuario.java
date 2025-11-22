package com.voluntariado.plataforma.model;

import com.voluntariado.plataforma.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    @Indexed(unique = true)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private String telefono;

    private String direccion;

    @Builder.Default
    private Rol rol = Rol.VOLUNTARIO;

    @Builder.Default
    private List<String> habilidades = new ArrayList<>();

    @Builder.Default
    private List<String> areasInteres = new ArrayList<>();

    @Builder.Default
    private boolean activo = true;

    @Builder.Default
    private boolean verificado = false;

    private String tokenVerificacion;

    private String tokenRecuperacion;

    private LocalDateTime tokenRecuperacionExpira;

    @Builder.Default
    private boolean notificacionesActivas = true;

    private String organizacionId;

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    private LocalDateTime ultimoAcceso;
}
