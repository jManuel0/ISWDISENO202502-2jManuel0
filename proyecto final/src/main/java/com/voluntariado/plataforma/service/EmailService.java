package com.voluntariado.plataforma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void enviarNotificacion(String destinatario, String asunto, String contenido) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("[Voluntariado ONG] " + asunto);
            message.setText(contenido);

            mailSender.send(message);
            log.info("Email enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    public void enviarEmailHtml(String destinatario, String asunto, String contenidoHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("[Voluntariado ONG] " + asunto);
            helper.setText(contenidoHtml, true);

            mailSender.send(message);
            log.info("Email HTML enviado a: {}", destinatario);
        } catch (MessagingException e) {
            log.error("Error al enviar email HTML a {}: {}", destinatario, e.getMessage());
        }
    }

    public void enviarEmailVerificacion(String destinatario, String nombre, String tokenVerificacion) {
        String asunto = "Verifica tu cuenta";
        String contenido = String.format("""
                Hola %s,

                Gracias por registrarte en nuestra plataforma de voluntariado.

                Por favor, verifica tu cuenta haciendo clic en el siguiente enlace:
                http://localhost:8080/api/auth/verificar?token=%s

                Si no creaste esta cuenta, ignora este mensaje.

                Saludos,
                Equipo de Voluntariado ONG
                """, nombre, tokenVerificacion);

        enviarNotificacion(destinatario, asunto, contenido);
    }

    public void enviarEmailRecuperacionPassword(String destinatario, String nombre, String tokenRecuperacion) {
        String asunto = "Recuperación de contraseña";
        String contenido = String.format("""
                Hola %s,

                Hemos recibido una solicitud para restablecer tu contraseña.

                Utiliza el siguiente enlace para crear una nueva contraseña:
                http://localhost:8080/recuperar-password?token=%s

                Este enlace expirará en 24 horas.

                Si no solicitaste este cambio, ignora este mensaje.

                Saludos,
                Equipo de Voluntariado ONG
                """, nombre, tokenRecuperacion);

        enviarNotificacion(destinatario, asunto, contenido);
    }

    public void enviarCertificado(String destinatario, String nombre, String nombreActividad, byte[] pdfAdjunto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("[Voluntariado ONG] Tu certificado de participación");

            String contenido = String.format("""
                    <html>
                    <body>
                    <h2>¡Felicitaciones %s!</h2>
                    <p>Adjuntamos tu certificado de participación en la actividad: <strong>%s</strong></p>
                    <p>Gracias por tu compromiso con el voluntariado.</p>
                    <br>
                    <p>Saludos,<br>Equipo de Voluntariado ONG</p>
                    </body>
                    </html>
                    """, nombre, nombreActividad);

            helper.setText(contenido, true);
            helper.addAttachment("certificado.pdf",
                    new org.springframework.core.io.ByteArrayResource(pdfAdjunto));

            mailSender.send(message);
            log.info("Certificado enviado a: {}", destinatario);
        } catch (MessagingException e) {
            log.error("Error al enviar certificado a {}: {}", destinatario, e.getMessage());
        }
    }
}
