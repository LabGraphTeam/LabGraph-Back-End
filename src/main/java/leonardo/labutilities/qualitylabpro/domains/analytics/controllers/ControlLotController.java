package leonardo.labutilities.qualitylabpro.domains.analytics.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.ControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.ControlLotService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/control-lot")
@RestController
public class ControlLotController {
    private final ControlLotService controlLotService;

    public ControlLotController(ControlLotService controlLotService) {
        this.controlLotService = controlLotService;
    }

    @PostMapping()
    public ResponseEntity<ControlLot> postControlLot(@RequestBody ControlLotDTO controlLot) {
        var response = controlLotService.createControlLot(controlLot);

        return ResponseEntity.created(URI.create("/control-lot/")).body(response);
    }

    @GetMapping()
    public ResponseEntity<List<ControlLot>> getControlLot() {
        return ResponseEntity.ok(controlLotService.getControlLot());
    }

}
