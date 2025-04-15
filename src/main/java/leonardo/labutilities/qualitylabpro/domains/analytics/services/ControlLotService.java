package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.util.List;

import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.ControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.ControlLotRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling.ResourceNotFoundException;

@Service
public class ControlLotService {
    private final ControlLotRepository controlLotRepository;

    public ControlLotService(ControlLotRepository controlLotRepository) {
        this.controlLotRepository = controlLotRepository;
    }

    public ControlLot createControlLot(ControlLotDTO controlLot) {

        var controlLotToSave = new ControlLot();

        controlLotToSave.setUser(controlLot.createdBy());
        controlLotToSave.setLotCode(controlLot.lotCode());
        controlLotToSave.setEquipments(controlLot.equipments());

        return controlLotRepository.save(controlLotToSave);
    }

    public List<ControlLot> getControlLot() {
        List<ControlLot> controlLots = controlLotRepository.findAll();
        if (controlLots.isEmpty()) {
            throw new ResourceNotFoundException("Control Lot not found");
        }
        return controlLots;
    }
}
