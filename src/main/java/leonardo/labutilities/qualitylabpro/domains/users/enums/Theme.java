package leonardo.labutilities.qualitylabpro.domains.users.enums;

public enum Theme {
    LIGHT("light"), DARK("dark"), SYSTEM("system");

    private final String value;

    Theme(String theme) {
        this.value = theme;
    }

    public String getValue() {
        return value;
    }
}
