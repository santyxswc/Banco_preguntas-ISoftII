/**
 * @file MultipleChoiceQuestionRecord.java
 * @brief JPA record for multiple choice questions with 1-to-many relationship to options.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Subclass entity mapped to table 'jpa_mc_questions'.
 *
 * Demonstrates the inverse side of the OneToMany relationship with mappedBy = "question".
 */
@Entity
@Table(name = "jpa_mc_questions")
public class MultipleChoiceQuestionRecord extends BaseQuestionRecord {

    @Column(name = "options_count")
    private int optionsCount;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionRecord> options = new ArrayList<>();

    public MultipleChoiceQuestionRecord() {}

    public void addOption(OptionRecord option) {
        options.add(option);
        option.setQuestion(this);
        this.optionsCount = options.size();
    }

    public void removeOption(OptionRecord option) {
        options.remove(option);
        option.setQuestion(null);
        this.optionsCount = options.size();
    }

    public int getOptionsCount() {
        return optionsCount;
    }

    public void setOptionsCount(int optionsCount) {
        this.optionsCount = optionsCount;
    }

    public List<OptionRecord> getOptions() {
        return options;
    }

    public void setOptions(List<OptionRecord> options) {
        this.options = options;
        if (options != null) {
            this.optionsCount = options.size();
        }
    }
}
