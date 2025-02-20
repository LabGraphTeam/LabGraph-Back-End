package leonardo.labutilities.qualitylabpro.domains.users.enums;

public enum QualityControlRulesApplied {
    RULE_1_3S("1-3s", "One observation exceeds mean ±3 SD"), RULE_4_1S("4-1s",
            "Four consecutive measurements exceed ±1 SD on same side of mean"), RULE_10X("10x",
                    "Ten consecutive measurements on same side of mean"), NONE("none",
                            "No rule applied");

    private final String code;
    private final String description;

    QualityControlRulesApplied(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
