package leonardo.labutilities.qualitylabpro.domains.analytics.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    Optional<Equipment> findByWorkSector(WorkSectorEnum workSector);

    Optional<Equipment> findBySerialNumber(String serialNumber);

    Optional<Equipment> findByCommercialName(String commercialName);

    boolean existsByCommercialName(String commercialName);
}
