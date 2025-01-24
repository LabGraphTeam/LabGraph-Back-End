package leonardo.labutilities.qualitylabpro.utils.constants;

public class EmailTemplate {
	public static final String EMAIL_SUBJECT_PREFIX = "LabGraph - ";
	public static final String HTML_TEMPLATE = "<html><head></head><body>%s</body></html>";
	public static final String TABLE_STYLE = """
			<style>
			    table {
			        border-collapse: collapse;
			        width: 100%%;
			        margin: 25px 0;
			        font-family: Arial, sans-serif;
			        box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
			        border-radius: 8px;
			        overflow: hidden;
			    }

			    th, td {
			        padding: 12px 15px;
			        text-align: left;
			        border-bottom: 1px solid #dddddd;
			    }

			    th {
			        background-color:rgb(0, 89, 149);
			        color: #ffffff;
			        font-weight: bold;
			    }

			    tr:nth-child(even) {
			        background-color: #f3f3f3;
			    }

			    td {
			        color: #333333;
			    }
			</style>
			""";
	public static final String ANALYTICS_WARNING_HEADER =
			"""
					    <p style="font-size: 16px; color: #d32f2f; margin-bottom: 20px;">
					        <strong>⚠️ Quality Control Alert: Westgard violations</strong>
					    </p>
					    <p style="color: #424242; margin-bottom: 15px;">
					        The following laboratory control measurements have triggered quality control violations based on
					        Westgard multi-rules. These violations may indicate systematic or random errors in the analytical process:
					    </p>
					""";

	public static final String FAILED_ANALYTICS_HEADER =
			"""
					<table><tr><th>Name</th><th>Level</th><th>Value</th><th>Expected Value</th><th>Rules</th><th>Status</th><th>Date</th></tr>
					""";
	public static final String LAST_ANALYTICS_PARAGRAPH = """
			</table><p>Please take the necessary actions to address these issues.</p>
			""";
}
