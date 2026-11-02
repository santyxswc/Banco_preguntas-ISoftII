package co.unicauca.iso2.taller09.questionservice.config;

import co.unicauca.iso2.taller09.questionservice.domain.QuestionBuilder;
import co.unicauca.iso2.taller09.questionservice.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Carga preguntas de ejemplo en borrador para la demostración (autores 2 y 3 del user-service).
 * Se desactiva con {@code questions.demo-data.enabled=false}.
 */
@Component
@ConditionalOnProperty(name = "questions.demo-data.enabled", havingValue = "true", matchIfMissing = true)
public class DemoDataLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);

    private final QuestionRepository repository;

    public DemoDataLoader(QuestionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(QuestionBuilder.aQuestion()
                .context("Una aplicación de banca debe avisar a varios módulos (correo, auditoría, reportes) cuando cambia el saldo de una cuenta.")
                .statement("¿Qué patrón de diseño permite notificar automáticamente a varios objetos cuando cambia el estado de otro?")
                .option("A", "Observer").option("B", "Singleton").option("C", "Builder").option("D", "Adapter")
                .correctOption("A")
                .justification("Observer define una dependencia uno a muchos: cuando el sujeto cambia, todos sus observadores son notificados.")
                .bibliography("Gamma, E. et al. Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley, 1994.")
                .competency("Diseño de software").topic("Patrones de diseño").subtopic("Patrones de comportamiento")
                .difficulty("MEDIO").author(2L).build());
        repository.save(QuestionBuilder.aQuestion()
                .context("Un equipo divide un monolito en microservicios y necesita que cada uno evolucione y escale de forma independiente.")
                .statement("¿Qué principio de DDD ayuda a definir los límites de cada microservicio?")
                .option("A", "Contexto delimitado (Bounded Context)").option("B", "Herencia múltiple")
                .option("C", "Normalización de tablas").option("D", "Programación estructurada")
                .correctOption("A")
                .justification("Un bounded context delimita un modelo y su lenguaje ubicuo, lo que da un límite natural para un servicio.")
                .bibliography("Evans, E. Domain-Driven Design. Addison-Wesley, 2003.")
                .competency("Diseño de software").topic("Arquitectura de software").subtopic("Microservicios")
                .difficulty("ALTO").author(2L).build());
        repository.save(QuestionBuilder.aQuestion()
                .context("En una base de datos relacional se repite el nombre del cliente en cada fila de la tabla de pedidos.")
                .statement("¿Qué forma normal elimina las dependencias transitivas entre atributos no clave?")
                .option("A", "Tercera forma normal").option("B", "Primera forma normal")
                .option("C", "Segunda forma normal").option("D", "Forma normal de dominio")
                .correctOption("A")
                .justification("La 3FN exige que ningún atributo no clave dependa transitivamente de la clave primaria.")
                .bibliography("Elmasri, R. y Navathe, S. Fundamentos de Sistemas de Bases de Datos. Pearson, 2016.")
                .competency("Bases de datos").topic("Modelo relacional").subtopic("Normalización")
                .difficulty("BAJO").author(3L).build());
        log.info("Datos de demostración cargados: {} preguntas", repository.count());
    }
}
