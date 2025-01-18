package leonardo.labutilities.qualitylabpro.controllers.analytics;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.dtos.analytics.*;
import leonardo.labutilities.qualitylabpro.services.analytics.AnalyticsHelperService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/generic-analytics")
@RestController()
public abstract class AnalyticsController extends AnalyticsHelperController {
    private final AnalyticsHelperService analyticsHelperService;

    public AnalyticsController(AnalyticsHelperService analyticsHelperService) {
        super(analyticsHelperService);
        this.analyticsHelperService = analyticsHelperService;
    }

    @GetMapping("/level-date-range")
    public abstract ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsByLevelDateRange(
            @RequestParam String level,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            @ParameterObject Pageable pageable);

    @PostMapping
    @Transactional
    public ResponseEntity<List<AnalyticsRecord>> postAnalytics(
            @Valid @RequestBody List<@Valid AnalyticsRecord> values) {
        analyticsHelperService.saveNewAnalyticsRecords(values);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping()
    public ResponseEntity<Void>
    updateAnalyticsMean(@Valid @RequestBody UpdateAnalyticsMeanRecord updateAnalyticsMeanRecord) {
        analyticsHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(
                updateAnalyticsMeanRecord.name(),
                updateAnalyticsMeanRecord.level(),
                updateAnalyticsMeanRecord.levelLot(),
                updateAnalyticsMeanRecord.mean());
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAnalyticsResultById(@PathVariable Long id) {
        analyticsHelperService.deleteAnalyticsById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grouped-by-level")
    public ResponseEntity<List<GroupedResultsByLevel>> getGroupedByLevel
            (@RequestParam String name,
             @RequestParam("startDate") LocalDateTime startDate,
             @RequestParam("endDate") LocalDateTime endDate) {
        List<GroupedResultsByLevel> groupedData =
                analyticsHelperService.findAnalyticsWithGroupedResults(name, startDate, endDate);
        return ResponseEntity.ok(groupedData);
    }

    @GetMapping("/grouped-by-level/mean-deviation")
    public ResponseEntity<List<GroupedMeanAndStdRecordByLevel>> getMeanAndDeviationGrouped(
            @RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        List<GroupedMeanAndStdRecordByLevel> groupedData = analyticsHelperService
                .calculateGroupedMeanAndStandardDeviation(name, startDate, endDate);
        return ResponseEntity.ok(groupedData);
    }

    @GetMapping("/name")
    public ResponseEntity<CollectionModel<EntityModel<AnalyticsRecord>>> getAllAnalyticsByName(
            @RequestParam String name, Pageable pageable) {
        List<AnalyticsRecord> resultsList = analyticsHelperService.findAnalyticsByNameWithPagination(pageable, name);

        List<EntityModel<AnalyticsRecord>> resultModels = resultsList.stream()
                .map(result -> createEntityModel(result, pageable))
                .toList();

        CollectionModel<EntityModel<AnalyticsRecord>> collectionModel = CollectionModel.of(resultModels,
                linkTo(methodOn(getClass()).getAllAnalyticsByName(name, pageable)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/date-range")
    public abstract ResponseEntity<CollectionModel<EntityModel<AnalyticsRecord>>> getAnalyticsDateBetween(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate, @PageableDefault(sort = "date", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable);


    @GetMapping("/name-and-level-date-range")
    public abstract ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsByNameAndLevelDateRange(
            @RequestParam String name, @RequestParam String level,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate);

    @GetMapping("/mean-standard-deviation")
    public abstract ResponseEntity<MeanAndStdDeviationRecord> getMeanAndStandardDeviation(
            @RequestParam String name, @RequestParam String level,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate);
}


