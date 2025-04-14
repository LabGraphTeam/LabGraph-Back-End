package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.util.List;

import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.CreateEquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.EquipmentRepository;
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

    public Equipment saveEquipment(CreateEquipmentDTO createEquipmentDTO) {

        var equipment = EquipmentMapper.mapToEquipment(createEquipmentDTO);
        return equipmentRepository.save(equipment);
    }

    public Equipment findByWorkSector(WorkSectorEnum workSector) {
        return equipmentRepository
                .findByWorkSector(workSector).orElseThrow(ResourceNotFoundException::new);
    }
}
