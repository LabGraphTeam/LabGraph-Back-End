package leonardo.labutilities.qualitylabpro.entities;

import jakarta.persistence.*;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.utils.components.RulesValidatorComponent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "generic_analytics")
public class Analytic extends RepresentationModel<Analytic> {

    LocalDateTime date;
    @Column(name = "level_lot")
    String levelLot;
    @Column(name = "test_lot")
    String testLot;
    String name;
    String level;
    double value;
    double mean;
    double sd;
    @Column(name = "unit_value")
    String unitValue;
    String rules;
    String description;
    @Transient
    RulesValidatorComponent rulesValidatorComponent;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Analytic() {
    }

    public Analytic(Long id, LocalDateTime date, String levelLot, String testLot,
                    String name, String level, double value, double mean, double sd, String unitValue,
                    String rules, String description, RulesValidatorComponent rulesValidatorComponent) {
        this.id = id;
        this.date = date;
        this.levelLot = levelLot;
        this.testLot = testLot;
        this.name = name;
        this.level = level;
        this.value = value;
        this.mean = mean;
        this.sd = sd;
        this.unitValue = unitValue;
        this.rules = rules;
        this.description = description;
        this.rulesValidatorComponent = rulesValidatorComponent;
    }

    public Analytic(AnalyticsDTO values,
                    RulesValidatorComponent rulesValidatorComponent) {
        this.date = values.date();
        this.levelLot = values.level_lot();
        this.testLot = values.test_lot();
        this.name = values.name();
        this.level = values.level();
        this.value = values.value();
        this.mean = values.mean();
        this.sd = values.sd();
        this.unitValue = values.unit_value();
        this.rulesValidatorComponent = rulesValidatorComponent;
        rulesValidatorComponent.validator(value, mean, sd);
        this.rules = rulesValidatorComponent.getRules();
        this.description = rulesValidatorComponent.getDescription();
    }
}
