package co.unicauca.iso2.bancopreguntas.domain;

import java.util.List;
import java.util.Optional;

/**
 * Abstracción para la persistencia de usuarios.
 * Siguiendo DIP, AuthService y GUILogin dependen de esta interfaz,
 * no de la implementación concreta.
 */
public interface UsuarioRepository {

    /**
     * Busca un usuario por email y contraseña (autenticación).
     *
     * @param email    email del usuario
     * @param password contraseña en texto plano
     * @return Optional con el usuario si las credenciales son válidas
     */
    Optional<Usuario> findByEmailAndPassword(String email, String password);

    /**
     * Obtiene todos los usuarios con un rol específico.
     * Usado para listar docentes/revisores disponibles (RF04).
     *
     * @param rol rol a filtrar
     * @return lista de usuarios con ese rol
     */
    List<Usuario> findByRol(Rol rol);

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return usuario encontrado o null
     */
    Usuario findById(String id);
}
