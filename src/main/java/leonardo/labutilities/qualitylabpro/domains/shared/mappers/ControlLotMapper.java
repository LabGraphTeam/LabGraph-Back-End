package leonardo.labutilities.qualitylabpro.domains.shared.mappers;

import org.springframework.stereotype.Component;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.ControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

@Component
public class ControlLotMapper {
    private ControlLotMapper() {}

    public static ControlLot toEntity(ControlLotDTO controlLotDTO) {

        return new ControlLot(
                new User(),
                controlLotDTO.lotCode(),
                controlLotDTO.manufactureDate(),
                controlLotDTO.expirationTime());
    }
}
