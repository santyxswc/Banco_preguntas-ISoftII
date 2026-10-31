/**
 * @file PreguntaMemoryRepository.java
 * @brief Implementación en memoria del contrato puro de repositorio para pruebas unitarias rápidas.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @brief Repositorio volátil en memoria para aislar pruebas del dominio DDD sin frameworks.
 */
public class PreguntaMemoryRepository implements PreguntaRepository {

    private final Map<Long, Pregunta> data = new ConcurrentHashMap<>();
    private final AtomicLong secId = new AtomicLong(100);

    @Override
    public Pregunta save(Pregunta pregunta) {
        if (pregunta.getId() == null) {
            pregunta.setId(secId.incrementAndGet());
        }
        data.put(pregunta.getId(), pregunta);
        return pregunta;
    }

    @Override
    public Optional<Pregunta> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Pregunta> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Pregunta> findByEstado(EstadoPregunta estado) {
        return data.values().stream()
                .filter(p -> p.getEstado() == estado)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        data.remove(id);
    }
}
