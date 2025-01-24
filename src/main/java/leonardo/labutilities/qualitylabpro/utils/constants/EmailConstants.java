package leonardo.labutilities.qualitylabpro.utils.constants;

public class EmailConstants {
    public static final String EMAIL_SUBJECT_PREFIX = "LabGraph - ";
    public static final String HTML_TEMPLATE = "<html><head></head><body>%s</body></html>";
    public static final String TABLE_STYLE = """
    <style>
        table { border-collapse: collapse; width: 100%%; }
        th, td { border: 1px solid black; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        tr:nth-child(even) { background-color: #f9f9f9; }
    </style>
    """;
}