/**
 * @file UsuarioRepository.java
 * @brief Contrato de persistencia de usuarios.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.List;
import java.util.Optional;

/**
 * @brief Abstracción de persistencia para Usuario.
 */
public interface UsuarioRepository {

    /**
     * @brief Busca un usuario por sus credenciales.
     * @param email    correo del usuario
     * @param password contraseña
     * @return el usuario si las credenciales son válidas
     */
    Optional<Usuario> findByEmailAndPassword(String email, String password);

    /**
     * @param rol rol a filtrar
     * @return usuarios con ese rol
     */
    List<Usuario> findByRol(Rol rol);

    /**
     * @param id identificador del usuario
     * @return el usuario o null si no existe
     */
    Usuario findById(String id);
}
