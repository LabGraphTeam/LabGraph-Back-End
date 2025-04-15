package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.ControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.ControlLotRepository;

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
}
