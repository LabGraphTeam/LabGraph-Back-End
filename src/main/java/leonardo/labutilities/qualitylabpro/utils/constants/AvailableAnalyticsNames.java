package leonardo.labutilities.qualitylabpro.utils.constants;

import java.util.ArrayList;
import java.util.List;

public final class AvailableAnalyticsNames {
    public static final List<String> ALL_ANALYTICS;

    static {
        List<String> allAnalytics = new ArrayList<>();
        allAnalytics.addAll(AvailableHematologyAnalytics.DEFAULT_HEMATO_ANALYTICS);
        allAnalytics.addAll(AvailableCoagulationAnalytics.DEFAULT_COAG_ANALYTICS);
        allAnalytics.addAll(AvailableBiochemistryAnalytics.DEFAULT_BIO_ANALYTICS);
        ALL_ANALYTICS = List.copyOf(allAnalytics);
    }

    private AvailableAnalyticsNames() {
        throw new UnsupportedOperationException("Cannot instantiate AnalyticsNames");
    }
}
