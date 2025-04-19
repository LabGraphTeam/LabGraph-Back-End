package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record CreateControlLotDTO(
        Integer id,
        String createdBy,
        String lotCode,
        LocalDate manufactureDate,
        LocalDate expirationTime,
        Integer equipment) {

}
