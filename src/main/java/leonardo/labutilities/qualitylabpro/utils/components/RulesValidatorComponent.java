package leonardo.labutilities.qualitylabpro.utils.components;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RulesValidatorComponent {

    String description;
    String rules;

    public void validator(Double value, Double mean, Double sd) {
        double[] thresholds = {mean + sd, mean + 2 * sd, mean + 3 * sd, mean - sd, mean - 2 * sd, mean - 3 * sd};
        String[] descriptions = {"Approved", "Approved", "Failed", "Approved", "Approved", "Failed"};
        String[] rules = {"+1s", "+2s", "+3s", "-1s", "-2s", "-3s"};

        for (int i = 2; i >= 0; i--) {
            if (value >= thresholds[i] || value <= thresholds[i + 3]) {
                this.description = descriptions[i];
                this.rules = value >= thresholds[i] ? rules[i] : rules[i + 3];
                return;
            }
        }
        this.description = "Approved";
        this.rules = "Average";
    }
}