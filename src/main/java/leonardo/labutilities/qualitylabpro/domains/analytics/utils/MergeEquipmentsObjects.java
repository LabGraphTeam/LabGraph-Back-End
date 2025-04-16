package leonardo.labutilities.qualitylabpro.domains.analytics.utils;

import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;

public class MergeEquipmentsObjects {

    private MergeEquipmentsObjects() {}

    public static Equipment merge(Equipment to, Equipment from) {
        if (from == null) {
            return to;
        }

        if (to == null) {
            return from;
        }

        if (from.getCommercialName() != null) {
            to.setCommercialName(from.getCommercialName());
        }

        if (from.getSerialNumber() != null) {
            to.setSerialNumber(from.getSerialNumber());
        }

        if (from.getWorkSector() != null) {
            to.setWorkSector(from.getWorkSector());
        }

        return to;
    }
}
