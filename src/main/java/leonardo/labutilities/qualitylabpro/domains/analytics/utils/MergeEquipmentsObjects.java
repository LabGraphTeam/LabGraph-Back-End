package leonardo.labutilities.qualitylabpro.domains.analytics.utils;

import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;

/**
 * Utility class for merging Equipment objects.
 */
public class MergeEquipmentsObjects {

    private MergeEquipmentsObjects() {}

    /**
     * Merges two Equipment objects, copying non-null values from 'from' to
     * 'to'.
     *
     * @param to The target Equipment object
     * @param from The source Equipment object
     * @return The merged Equipment object
     */
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
