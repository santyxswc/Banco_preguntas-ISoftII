package co.unicauca.iso2.bancopreguntas.access;

import co.unicauca.iso2.bancopreguntas.domain.Rol;
import co.unicauca.iso2.bancopreguntas.domain.Usuario;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación en memoria de {@link UsuarioRepository}.
 *
 * Carga un conjunto de usuarios de ejemplo (un administrador,
 * varios autores/docentes) para poder probar el login y la
 * asignación de revisores sin base de datos.
 *
 * Credenciales de ejemplo:
 * <pre>
 *   admin@unicauca.edu.co  / admin123   → ADMINISTRADOR
 *   autor1@unicauca.edu.co / autor123   → AUTOR
 *   autor2@unicauca.edu.co / autor123   → AUTOR
 *   revisor1@unicauca.edu.co / rev123  → AUTOR (docente revisor)
 *   revisor2@unicauca.edu.co / rev123  → AUTOR (docente revisor)
 * </pre>
 */
public class UsuarioImplRepository implements UsuarioRepository {

    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();

    public UsuarioImplRepository() {
        cargarUsuariosDeEjemplo();
    }

    @Override
    public Optional<Usuario> findByEmailAndPassword(String email,
                                                     String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }

        return usuarios.values().stream()
                .filter(u -> email.trim().equalsIgnoreCase(u.getEmail())
                          && password.equals(u.getPassword()))
                .findFirst();
    }

    @Override
    public List<Usuario> findByRol(Rol rol) {
        if (rol == null) {
            return new ArrayList<>();
        }
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario u : usuarios.values()) {
            if (rol == u.getRol()) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    @Override
    public Usuario findById(String id) {
        if (id == null) {
            return null;
        }
        return usuarios.get(id);
    }

    // ----------------------------------------------------------------
    // Datos de ejemplo
    // ----------------------------------------------------------------

    private void cargarUsuariosDeEjemplo() {

        agregar("U-001", "Admin Sistema",
                "admin@unicauca.edu.co", "admin123",
                Rol.ADMINISTRADOR);

        agregar("U-002", "Carlos Morales",
                "autor1@unicauca.edu.co", "autor123",
                Rol.AUTOR);

        agregar("U-003", "Laura Pérez",
                "autor2@unicauca.edu.co", "autor123",
                Rol.AUTOR);

        agregar("U-004", "Marco Rivas",
                "revisor1@unicauca.edu.co", "rev123",
                Rol.AUTOR);

        agregar("U-005", "Diana Castro",
                "revisor2@unicauca.edu.co", "rev123",
                Rol.AUTOR);
    }

    private void agregar(String id, String nombre, String email,
                         String password, Rol rol) {
        usuarios.put(id, new Usuario(id, nombre, email, password, rol));
    }
}
