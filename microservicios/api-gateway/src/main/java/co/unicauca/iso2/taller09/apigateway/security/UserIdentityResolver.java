package co.unicauca.iso2.taller09.apigateway.security;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida del gateway hacia el contexto de Identidad: a partir de la identidad del JWT
 * devuelve el perfil local del usuario, cuyo identificador numérico es el que usan los demás
 * microservicios (autor de una pregunta, revisor asignado, destinatario de un correo).
 */
public interface UserIdentityResolver {

    Mono<UserDTO> resolve(KeycloakIdentity identity);
}
