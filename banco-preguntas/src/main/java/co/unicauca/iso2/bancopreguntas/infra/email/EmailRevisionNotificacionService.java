/**
 * @file EmailRevisionNotificacionService.java
 * @brief Notificación por correo al autor cuando su pregunta es evaluada (HU05).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.email;

import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;
import co.unicauca.iso2.bancopreguntas.domain.Usuario;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;
import co.unicauca.iso2.bancopreguntas.infra.events.EventListener;
import co.unicauca.iso2.bancopreguntas.infra.events.RevisionCompletadaEvent;

/**
 * @brief Suscriptor que notifica al autor cuando un revisor aprueba o rechaza su pregunta.
 */
public class EmailRevisionNotificacionService implements EventListener<RevisionCompletadaEvent> {

    private final UsuarioRepository usuarioRepository;
    private final QuestionRepository questionRepository;

    public EmailRevisionNotificacionService(UsuarioRepository usuarioRepository,
                                            QuestionRepository questionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void onEvent(RevisionCompletadaEvent evento) {
        if (evento == null) {
            return;
        }

        String autorEmail = "autor";
        if (questionRepository != null) {
            Question q = questionRepository.findById(evento.getPreguntaId());
            if (q != null && q.getAutorId() != null && usuarioRepository != null) {
                Usuario u = usuarioRepository.findById(q.getAutorId());
                if (u != null && u.getEmail() != null) {
                    autorEmail = u.getEmail();
                } else {
                    autorEmail = q.getAutorId();
                }
            }
        }

        System.out.println("[NOTIFICACION-REVISION] Correo para " + autorEmail
                + " | Pregunta: " + evento.getPreguntaId()
                + " | Veredicto: " + evento.getVeredicto()
                + " | Observaciones: " + evento.getObservaciones());
    }
}
