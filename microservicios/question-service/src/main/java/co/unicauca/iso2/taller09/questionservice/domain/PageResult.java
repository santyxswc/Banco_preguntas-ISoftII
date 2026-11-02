package co.unicauca.iso2.taller09.questionservice.domain;

import java.util.List;
import java.util.function.Function;

/** Página de resultados: elementos, número de página (desde 0), tamaño y totales. */
public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResult<T> of(List<T> all, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw QuestionException.badRequest("Paginación inválida: page >= 0 y 1 <= size <= 100");
        }
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        int totalPages = (int) Math.ceil(all.size() / (double) size);
        return new PageResult<>(List.copyOf(all.subList(from, to)), page, size, all.size(), totalPages);
    }

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(content.stream().map(mapper).toList(), page, size, totalElements, totalPages);
    }
}
