package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.auth.AuthResponse;
import com.voluntariado.plataforma.dto.auth.LoginRequest;
import com.voluntariado.plataforma.dto.auth.RegistroRequest;
import com.voluntariado.plataforma.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo voluntario")
    public ResponseEntity<ApiResponse<AuthResponse>> registrar(@Valid @RequestBody RegistroRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.ok(ApiResponse.success("Registro exitoso", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }

    @PostMapping("/recuperar-password")
    @Operation(summary = "Solicitar recuperación de contraseña")
    public ResponseEntity<ApiResponse<Void>> solicitarRecuperacion(@RequestParam String correo) {
        authService.solicitarRecuperacionPassword(correo);
        return ResponseEntity.ok(ApiResponse.success("Se ha enviado un correo con las instrucciones", null));
    }

    @PostMapping("/restablecer-password")
    @Operation(summary = "Restablecer contraseña con token")
    public ResponseEntity<ApiResponse<Void>> restablecerPassword(
            @RequestParam String token,
            @RequestParam String nuevaPassword) {
        authService.recuperarPassword(token, nuevaPassword);
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada exitosamente", null));
    }
}
