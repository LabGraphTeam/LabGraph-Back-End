package leonardo.labutilities.qualitylabpro.utils.components;

import java.util.List;

public class ControlRulesValidator {

    public boolean rule1_2s(List<Double> values, double mean, double stdDev) {
        return values.stream().anyMatch(value -> Math.abs(value - mean) > 2 * stdDev);
    }

    public boolean rule1_3s(List<Double> values, double mean, double stdDev) {
        return values.stream().anyMatch(value -> Math.abs(value - mean) > 3 * stdDev);
    }

    public boolean rule2_2s(List<Double> values, double mean, double stdDev) {
        for (int i = 1; i < values.size(); i++) {
            if (Math.abs(values.get(i) - mean) > 2 * stdDev && Math.abs(values.get(i - 1) - mean) > 2 * stdDev &&
                (values.get(i) - mean) * (values.get(i - 1) - mean) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean ruleR_4s(List<Double> values, double mean, double stdDev) {
        for (int i = 1; i < values.size(); i++) {
            if (Math.abs(values.get(i) - values.get(i - 1)) > 4 * stdDev) {
                return true;
            }
        }
        return false;
    }

    public boolean rule4_1s(List<Double> values, double mean, double stdDev) {
        int count = 0;
        for (double value : values) {
            if (Math.abs(value - mean) > stdDev) {
                count++;
                if (count >= 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }

    public boolean rule10x(List<Double> values, double mean) {
        if (values.size() < 10) {
            return false;
        }
        int countAbove = 0;
        int countBelow = 0;
        for (double value : values) {
            if (value > mean) {
                countAbove++;
                countBelow = 0;
            } else if (value < mean) {
                countBelow++;
                countAbove = 0;
            } else {
                countAbove = 0;
                countBelow = 0;
            }
            if (countAbove >= 10 || countBelow >= 10) {
                return true;
            }
        }
        return false;
    }
}