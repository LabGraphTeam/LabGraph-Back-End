package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.util.List;

import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.ControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.CreateControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.ControlLotRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.utils.AuthenticatedUserProvider;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling.ResourceNotFoundException;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

@Service
public class ControlLotService {
    private final ControlLotRepository controlLotRepository;
    private final EquipmentService equipmentService;

    public ControlLotService(ControlLotRepository controlLotRepository, EquipmentService equipmentService) {
        this.controlLotRepository = controlLotRepository;
        this.equipmentService = equipmentService;
    }

    public ControlLot createControlLot(CreateControlLotDTO controlLot) {

        User user = AuthenticatedUserProvider.getCurrentAuthenticatedUser();

        Equipment equipment = equipmentService.findById(controlLot.equipment());

        var controlLotToSave = new ControlLot();

        controlLotToSave.setUser(user);
        controlLotToSave.setLotCode(controlLot.lotCode());
        controlLotToSave.setManufactureDate(controlLot.manufactureDate());
        controlLotToSave.setExpirationTime(controlLot.expirationTime());
        controlLotToSave.setEquipment(equipment);

        return controlLotRepository.save(controlLotToSave);
    }

    public List<ControlLotDTO> getControlLots() {

        List<ControlLot> controlLots = controlLotRepository.findAll();

        if (controlLots.isEmpty()) {
            throw new ResourceNotFoundException("Control Lot not found");
        }

        return controlLots.stream().map(ControlLotDTO::new).toList();
    }
}
