# Plataforma de Voluntariado ONG

Sistema de gestión de voluntariado para ONGs desarrollado con Spring Boot y MongoDB Atlas.

## Tecnologías

- **Backend**: Java 17 + Spring Boot 3.2
- **Base de datos**: MongoDB Atlas
- **Autenticación**: JWT
- **Documentación API**: Swagger/OpenAPI
- **PDF**: iText 7
- **Email**: Spring Mail

## Requisitos

- Java 17+
- Maven 3.8+
- Conexión a internet (MongoDB Atlas)

## Configuración

La conexión a MongoDB Atlas ya está configurada en `application.properties`.

## Ejecutar el proyecto

```bash
cd "C:\Users\JUAN MANUEL\voluntariado-ong"
mvn spring-boot:run
```

O compilar y ejecutar:
```bash
mvn clean package
java -jar target/plataforma-voluntariado-1.0.0.jar
```

## URLs importantes

| Recurso | URL |
|---------|-----|
| API Base | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/api-docs |

## Usuarios de prueba

| Rol | Correo | Contraseña |
|-----|--------|------------|
| Administrador | admin@ong.org | admin123 |
| Coordinador | coordinador@ong.org | coord123 |
| Voluntario | voluntario@test.com | vol123 |

## Endpoints principales

### Autenticación (`/api/auth`)
- `POST /registro` - Registrar nuevo voluntario
- `POST /login` - Iniciar sesión
- `POST /recuperar-password` - Solicitar recuperación
- `POST /restablecer-password` - Restablecer contraseña

### Usuarios (`/api/usuarios`)
- `GET /perfil` - Obtener perfil actual
- `PUT /perfil` - Actualizar perfil
- `POST /cambiar-password` - Cambiar contraseña
- `GET /` - Listar usuarios (Admin)
- `PUT /{id}/rol` - Cambiar rol (Admin)

### Actividades (`/api/actividades`)
- `GET /` - Listar todas
- `GET /disponibles` - Listar con cupos disponibles
- `GET /{id}` - Obtener por ID
- `GET /buscar` - Buscar por título/lugar
- `POST /` - Crear actividad (Coordinador)
- `PUT /{id}` - Actualizar actividad
- `DELETE /{id}` - Eliminar actividad

### Inscripciones (`/api/inscripciones`)
- `POST /actividad/{id}` - Inscribirse
- `DELETE /actividad/{id}` - Cancelar inscripción
- `GET /mis-inscripciones` - Mis inscripciones
- `PATCH /{id}/aprobar` - Aprobar (Coordinador)
- `PATCH /{id}/asistencia` - Registrar asistencia

### Historial (`/api/historial`)
- `GET /` - Mi historial
- `GET /estadisticas` - Mis estadísticas

### Certificados (`/api/certificados`)
- `GET /` - Mis certificados
- `POST /generar` - Generar certificado
- `GET /{id}/descargar` - Descargar PDF
- `GET /verificar/{codigo}` - Verificar autenticidad

### Notificaciones (`/api/notificaciones`)
- `GET /` - Mis notificaciones
- `GET /no-leidas` - No leídas
- `PATCH /{id}/leer` - Marcar como leída

### Mensajes (`/api/mensajes`)
- `POST /` - Enviar mensaje
- `GET /bandeja-entrada` - Bandeja de entrada
- `GET /enviados` - Mensajes enviados

### Administración (`/api/admin`)
- `GET /estadisticas` - Estadísticas globales
- `GET /auditoria` - Logs de auditoría

## Estructura del proyecto

```
src/main/java/com/voluntariado/plataforma/
├── config/          # Configuraciones (Security, MongoDB, OpenAPI)
├── controller/      # Controladores REST
├── dto/             # Data Transfer Objects
├── exception/       # Manejo de excepciones
├── model/           # Entidades/Modelos
│   └── enums/       # Enumeraciones
├── repository/      # Repositorios MongoDB
├── security/        # JWT y autenticación
└── service/         # Lógica de negocio
```

## Historias de Usuario implementadas

- [x] HU-01: Registro de voluntarios
- [x] HU-02: Inicio de sesión
- [x] HU-03: Edición de perfil
- [x] HU-04: Gestión de usuarios (Admin)
- [x] HU-05: Recuperación de contraseña
- [x] HU-06: Creación de actividades
- [x] HU-07: Listado de actividades
- [x] HU-08: Inscripción a actividades
- [x] HU-09: Cancelación de inscripción
- [x] HU-10: Registro de asistencia
- [x] HU-11: Notificaciones de nuevas actividades
- [x] HU-12: Recordatorios automáticos
- [x] HU-13: Envío de comunicados
- [x] HU-14: Historial de participación
- [x] HU-15: Subida de evidencias
- [x] HU-16: Exportación de informes
- [x] HU-17: Filtrado de actividades
- [x] HU-18: Búsqueda de actividades
- [x] HU-20: Informe individual
- [x] HU-21: Estadísticas globales
- [x] HU-22: Evaluación de voluntarios
- [x] HU-23: Mensajería interna
- [x] HU-27: Asignación de roles
- [x] HU-29: Trazabilidad/Auditoría

## Configurar Email (Opcional)

Para habilitar notificaciones por email, edita `application.properties`:

```properties
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
```

Para Gmail, necesitas crear una "Contraseña de aplicación" en tu cuenta de Google.
