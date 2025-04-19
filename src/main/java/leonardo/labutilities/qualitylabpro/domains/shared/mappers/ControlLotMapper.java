package leonardo.labutilities.qualitylabpro.domains.shared.mappers;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.CreateControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

public class ControlLotMapper {
    private ControlLotMapper() {}

    public static ControlLot toEntity(CreateControlLotDTO controlLotDTO) {

        return new ControlLot(
                new User(),
                controlLotDTO.lotCode(),
                controlLotDTO.manufactureDate(),
                controlLotDTO.expirationTime());
    }
}
