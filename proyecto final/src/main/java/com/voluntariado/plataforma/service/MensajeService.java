package com.voluntariado.plataforma.service;

import com.voluntariado.plataforma.dto.MensajeDTO;
import com.voluntariado.plataforma.exception.ResourceNotFoundException;
import com.voluntariado.plataforma.model.Mensaje;
import com.voluntariado.plataforma.model.Usuario;
import com.voluntariado.plataforma.repository.MensajeRepository;
import com.voluntariado.plataforma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    public MensajeDTO enviarMensaje(MensajeDTO dto, String remitenteId) {
        Mensaje mensaje = Mensaje.builder()
                .remitenteId(remitenteId)
                .destinatarioId(dto.getDestinatarioId())
                .asunto(dto.getAsunto())
                .contenido(dto.getContenido())
                .build();

        mensaje = mensajeRepository.save(mensaje);
        return convertirADTO(mensaje);
    }

    public List<MensajeDTO> obtenerBandejaSntrada(String usuarioId) {
        return mensajeRepository.findByDestinatarioIdOrderByFechaEnvioDesc(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<MensajeDTO> obtenerEnviados(String usuarioId) {
        return mensajeRepository.findByRemitenteIdAndEliminadoFalse(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<MensajeDTO> obtenerNoLeidos(String usuarioId) {
        return mensajeRepository.findByDestinatarioIdAndLeidoFalseAndEliminadoFalse(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public long contarNoLeidos(String usuarioId) {
        return mensajeRepository.countByDestinatarioIdAndLeidoFalseAndEliminadoFalse(usuarioId);
    }

    public MensajeDTO obtenerPorId(String id, String usuarioId) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje", "id", id));

        // Marcar como leído si el usuario es el destinatario
        if (mensaje.getDestinatarioId().equals(usuarioId) && !mensaje.isLeido()) {
            mensaje.setLeido(true);
            mensaje.setFechaLectura(LocalDateTime.now());
            mensajeRepository.save(mensaje);
        }

        return convertirADTO(mensaje);
    }

    public void archivarMensaje(String id, String usuarioId) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje", "id", id));

        mensaje.setArchivado(true);
        mensajeRepository.save(mensaje);
    }

    public void eliminarMensaje(String id, String usuarioId) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje", "id", id));

        mensaje.setEliminado(true);
        mensajeRepository.save(mensaje);
    }

    public List<MensajeDTO> obtenerArchivados(String usuarioId) {
        return mensajeRepository.findByDestinatarioIdAndArchivadoTrueAndEliminadoFalse(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private MensajeDTO convertirADTO(Mensaje mensaje) {
        String remitenteNombre = usuarioRepository.findById(mensaje.getRemitenteId())
                .map(Usuario::getNombre)
                .orElse(null);

        String destinatarioNombre = usuarioRepository.findById(mensaje.getDestinatarioId())
                .map(Usuario::getNombre)
                .orElse(null);

        return MensajeDTO.builder()
                .id(mensaje.getId())
                .remitenteId(mensaje.getRemitenteId())
                .remitenteNombre(remitenteNombre)
                .destinatarioId(mensaje.getDestinatarioId())
                .destinatarioNombre(destinatarioNombre)
                .asunto(mensaje.getAsunto())
                .contenido(mensaje.getContenido())
                .leido(mensaje.isLeido())
                .archivado(mensaje.isArchivado())
                .fechaEnvio(mensaje.getFechaEnvio())
                .fechaLectura(mensaje.getFechaLectura())
                .build();
    }
}
