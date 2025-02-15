package leonardo.labutilities.qualitylabpro.utils.constants;

import java.util.List;

public final class AvailableBiochemistryAnalytics {
	public static final List<String> DEFAULT_BIO_ANALYTICS =
			List.of("ALB2", "ALP2S", "ALTL", "AMYL2", "ASTL", "BILD2", "BILT3", "CA2", "CHOL2",
					"CK2", "CKMB2", "CL-I", "CREJ2", "CRP4", "GGTI2", "GLUC3", "HDLC4", "K-I",
					"LDHI2", "LIP", "MG-2", "NA-I", "PHOS2", "TRIGL", "UA2", "UREL");

	private AvailableBiochemistryAnalytics() {
		throw new UnsupportedOperationException(
				"Cannot instantiate AvailableBiochemistryAnalytics");
	}
}
