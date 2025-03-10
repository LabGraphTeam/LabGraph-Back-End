package leonardo.labutilities.qualitylabpro.domains.users.enums;

import lombok.Getter;

@Getter
public enum Theme {
    LIGHT("light"), DARK("dark"), SYSTEM("system");

    private final String value;

    Theme(String theme) {
        this.value = theme;
    }

}
