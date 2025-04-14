package leonardo.labutilities.qualitylabpro.domains.analytics.enums;

public enum WorkSectorEnum {
    HEMATOLOGY("Hematology"), BIOCHEMISTRY("Biochemistry"), IMMUNOLOGY("Immunology"), MICROBIOLOGY(
            "Microbiology"), URINALYSIS("Urinalysis"), BLOOD_BANK("Blood Bank"), MOLECULAR_BIOLOGY(
                    "Molecular Biology"), PATHOLOGY("Pathology"), CYTOLOGY("Cytology"), HISTOLOGY("Histology");

    private final String sector;

    WorkSectorEnum(String sector) {
        this.sector = sector;
    }

    public String getSector() {
        return sector;
    }

}
