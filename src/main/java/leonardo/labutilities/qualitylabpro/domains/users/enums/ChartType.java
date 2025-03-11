package leonardo.labutilities.qualitylabpro.domains.users.enums;

import lombok.Getter;

@Getter
public enum ChartType {

    SINGLE_LINE("single_line_chart"), MULTI_LINE("multi_line_chart");

    private final String type;

    ChartType(String type) {
        this.type = type;
    }

}
