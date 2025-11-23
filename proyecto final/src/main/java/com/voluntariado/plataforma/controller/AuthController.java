package com.voluntariado.plataforma.controller;

import com.voluntariado.plataforma.dto.ApiResponse;
import com.voluntariado.plataforma.dto.auth.AuthResponse;
import com.voluntariado.plataforma.dto.auth.LoginRequest;
import com.voluntariado.plataforma.dto.auth.RecuperarPasswordRequest;
import com.voluntariado.plataforma.dto.auth.RegistroRequest;
import com.voluntariado.plataforma.dto.auth.RestablecerPasswordRequest;
import com.voluntariado.plataforma.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<ApiResponse<Void>> solicitarRecuperacion(@Valid @RequestBody RecuperarPasswordRequest request) {
        authService.solicitarRecuperacionPassword(request.getCorreo());
        return ResponseEntity.ok(ApiResponse.success("Se ha enviado un correo con las instrucciones", null));
    }

    @PostMapping("/verificar-codigo")
    @Operation(summary = "Verificar código de recuperación")
    public ResponseEntity<ApiResponse<Map<String, String>>> verificarCodigo(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String codigo = request.get("codigo");
        String token = authService.verificarCodigoRecuperacion(correo, codigo);
        return ResponseEntity.ok(ApiResponse.success("Código verificado", Map.of("token", token)));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña con token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody RestablecerPasswordRequest request) {
        authService.recuperarPassword(request.getToken(), request.getNuevaPassword());
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada exitosamente", null));
    }

    @PostMapping("/restablecer-password")
    @Operation(summary = "Restablecer contraseña con token (alternativo)")
    public ResponseEntity<ApiResponse<Void>> restablecerPassword(@Valid @RequestBody RestablecerPasswordRequest request) {
        authService.recuperarPassword(request.getToken(), request.getNuevaPassword());
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada exitosamente", null));
    }
}
