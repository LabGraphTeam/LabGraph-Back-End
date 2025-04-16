package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.ControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.ControlLotRepository;
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

    public ControlLot createControlLot(ControlLotDTO controlLot) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User)) {
            throw new BadCredentialsException("User not authenticated");
        }

        User user = (User) authentication.getPrincipal();
        Equipment equipment = equipmentService.findById(controlLot.equipmentId());

        var controlLotToSave = new ControlLot();

        controlLotToSave.setUser(user);
        controlLotToSave.setLotCode(controlLot.lotCode());
        controlLotToSave.setManufactureDate(controlLot.manufactureDate());
        controlLotToSave.setExpirationTime(controlLot.expirationTime());
        controlLotToSave.setEquipmentId(equipment);

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
