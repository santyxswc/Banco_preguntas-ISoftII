package co.unicauca.iso2.taller09.questionservice.repository;

import co.unicauca.iso2.taller09.questionservice.domain.Question;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class QuestionRepository {
    private final Map<Long, Question> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(100);

    public Question save(Question question) {
        if (question.getId() == null) {
            question.setId(idGenerator.incrementAndGet());
        }
        storage.put(question.getId(), question);
        return question;
    }

    public Optional<Question> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Question> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Question> findByAuthorId(Long authorId) {
        return storage.values().stream()
                .filter(q -> authorId.equals(q.getAuthorId()))
                .toList();
    }
}
