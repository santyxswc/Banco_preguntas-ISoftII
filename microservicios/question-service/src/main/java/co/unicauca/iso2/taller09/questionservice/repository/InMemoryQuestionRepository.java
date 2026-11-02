package co.unicauca.iso2.taller09.questionservice.repository;

import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryQuestionRepository implements QuestionRepository {
    private final Map<Long, Question> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(100);

    @Override
    public Question save(Question question) {
        if (question.getId() == null) {
            question.setId(idGenerator.incrementAndGet());
        }
        storage.put(question.getId(), question);
        return question;
    }

    @Override
    public Optional<Question> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Question> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Question> findByAuthorId(Long authorId) {
        return storage.values().stream()
                .filter(q -> authorId.equals(q.getAuthorId()))
                .toList();
    }

    @Override
    public List<Question> findMatching(QuestionFilter filter) {
        return storage.values().stream()
                .filter(filter::matchesContent)
                .sorted(Comparator.comparing(Question::getId).reversed())
                .toList();
    }

    @Override
    public long count() {
        return storage.size();
    }
}
