package co.unicauca.iso2.taller09.lifecycleservice.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data de los resultados de las operaciones asíncronas. */
public interface OperationResultJpaRepository extends JpaRepository<OperationResultEntity, String> {
}
