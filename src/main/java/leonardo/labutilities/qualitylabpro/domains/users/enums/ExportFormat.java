package leonardo.labutilities.qualitylabpro.domains.users.enums;

import lombok.Getter;

@Getter
public enum ExportFormat {

    CSV("CSV"), EXCEL("EXCEL"), PDF("PDF"), JSON("JSON");

    private final String format;

    ExportFormat(String format) {
        this.format = format;
    }

}
