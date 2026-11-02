package co.unicauca.iso2.taller09.questionservice.repository.jpa;

import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class QuestionSpecifications {

    private QuestionSpecifications() {
    }

    public static Specification<QuestionEntity> matching(QuestionFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.authorId() != null) {
                predicates.add(cb.equal(root.get("authorId"), filter.authorId()));
            }
            addEqualsIgnoreCase(predicates, cb, root.get("competency"), filter.competency());
            addEqualsIgnoreCase(predicates, cb, root.get("topic"), filter.topic());
            addEqualsIgnoreCase(predicates, cb, root.get("difficulty"), filter.difficulty());
            if (hasText(filter.keyword())) {
                String pattern = "%" + filter.keyword().trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("context")), pattern),
                        cb.like(cb.lower(root.get("statement")), pattern),
                        cb.like(cb.lower(root.get("topic")), pattern),
                        cb.like(cb.lower(root.get("subtopic")), pattern)));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static void addEqualsIgnoreCase(List<Predicate> predicates, jakarta.persistence.criteria.CriteriaBuilder cb,
                                            Expression<String> field, String value) {
        if (hasText(value)) {
            predicates.add(cb.equal(cb.lower(field), value.trim().toLowerCase(Locale.ROOT)));
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
