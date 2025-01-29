package leonardo.labutilities.qualitylabpro.utils.constants;

public class EmailTemplate {
	public static final String EMAIL_SUBJECT_PREFIX = "LabGraph - ";
	public static final String HTML_TEMPLATE = "<html><head></head><body>%s</body></html>";
	public static final String TABLE_STYLE = """
            <table style="border-collapse: collapse; width: 100%%; margin: 25px 0; font-family: Arial, sans-serif; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); border-radius: 8px; overflow: hidden;">
                <thead>
                    <tr style="background-color: rgb(0, 89, 149); color: #ffffff; font-weight: bold;">
                        <th style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">Name</th>
                        <th style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">Level</th>
                        <th style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">Value</th>
                        <th style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">Expected Value</th>
                        <th style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">Rules</th>
                        <th style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">Status</th>
                        <th style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">Date</th>
                    </tr>
                </thead>
                <tbody>
                    %s
                </tbody>
            </table>
            """;
	public static final String TABLE_ROW = """
            <tr>
                <td style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">%s</td>
                <td style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">%s</td>
                <td style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">%s</td>
                <td style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">%s</td>
                <td style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">%s</td>
                <td style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">%s</td>
                <td style="padding: 12px 15px; text-align: left; border-bottom: 1px solid #dddddd;">%s</td>
            </tr>""";
	public static final String ANALYTICS_WARNING_HEADER = """
            <p style="font-size: 16px; color: #d32f2f; margin-bottom: 20px;">
                <strong>⚠️ Quality Control Alert: Westgard violations</strong>
            </p>
            <p style="color: #424242; margin-bottom: 15px;">
                The following laboratory control measurements have triggered quality control violations based on
                Westgard multi-rules. These violations may indicate systematic or random errors in the analytical process:
            </p>
            """;
	public static final String LAST_ANALYTICS_PARAGRAPH = """
            <p>Please take the necessary actions to address these issues.</p>
            """;
}