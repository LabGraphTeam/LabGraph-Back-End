package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.util.List;

import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.EquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.EquipmentRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.utils.MergeEquipmentsObjects;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling.ResourceNotFoundException;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.EquipmentMapper;

@Service
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public Equipment findById(Integer id) {
        return equipmentRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    public Equipment saveEquipment(EquipmentDTO createEquipmentDTO) {

        var equipment = EquipmentMapper.toEntity(createEquipmentDTO);
        return equipmentRepository.save(equipment);
    }

    public Equipment findByWorkSector(WorkSectorEnum workSector) {
        return equipmentRepository
                .findByWorkSector(workSector).orElseThrow(ResourceNotFoundException::new);
    }

    public Equipment updateEquipment(Integer id, EquipmentDTO updateEquipmentDTO) {

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));

        Equipment updatedEquipment =
                MergeEquipmentsObjects.merge(equipment, EquipmentMapper.toEntity(updateEquipmentDTO));

        return equipmentRepository.save(updatedEquipment);
    }
}
