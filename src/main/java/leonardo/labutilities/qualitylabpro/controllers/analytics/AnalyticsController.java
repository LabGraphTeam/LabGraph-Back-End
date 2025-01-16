package leonardo.labutilities.qualitylabpro.controllers.analytics;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.dtos.analytics.*;
import leonardo.labutilities.qualitylabpro.services.analytics.AnalyticsHelperService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/generic-analytics")
@RestController()
public abstract class AnalyticsController {
    private final AnalyticsHelperService analyticsHelperService;
    private PagedResourcesAssembler<AnalyticsRecord> pagedResourcesAssembler;

    public AnalyticsController(AnalyticsHelperService analyticsHelperService) {
        this.analyticsHelperService = analyticsHelperService;
    }


    @GetMapping("/level-date-range")
    public abstract ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsByLevelDateRange(
            @RequestParam String level,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            Pageable pageable);


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAnalyticsResultById(@PathVariable Long id) {
        analyticsHelperService.deleteAnalyticsById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsRecord> getAnalyticsById(@PathVariable Long id) {
        return ResponseEntity.ok(analyticsHelperService.findOneById(id));
    }


    @PostMapping
    @Transactional
    public ResponseEntity<List<AnalyticsRecord>> postAnalytics(
            @Valid @RequestBody List<@Valid AnalyticsRecord> values) {
        analyticsHelperService.saveNewAnalyticsRecords(values);
        return ResponseEntity.status(201).build();
    }

    public ResponseEntity<CollectionModel<EntityModel<AnalyticsRecord>>> getAllAnalyticsWithLinks(List<String> names, Pageable pageable) {
        Page<AnalyticsRecord> resultsList = analyticsHelperService.findAnalyticsPagedByNameIn(names, pageable);

        // Create EntityModel for each record with its own self link
        var entityModels = resultsList.getContent().stream()
                .map(record -> EntityModel.of(record,
                        linkTo(methodOn(getClass()).getAnalyticsById(record.id())).withSelfRel()))
                .collect(Collectors.toList());

        var result = addPaginationLinks(CollectionModel.of(entityModels), resultsList, pageable);
        return ResponseEntity.ok(result);

    }

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<AnalyticsRecord>>> getAllAnalytics(
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AnalyticsRecord> resultsList = analyticsHelperService.findAnalytics(pageable);
        var model = pagedResourcesAssembler.toModel(resultsList);

        return ResponseEntity.ok(model);
    }

    @PatchMapping()
    public ResponseEntity<Void> updateAnalyticsMean(@Valid @RequestBody UpdateAnalyticsMeanRecord updateAnalyticsMeanRecord) {
        analyticsHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(
                updateAnalyticsMeanRecord.name(),
                updateAnalyticsMeanRecord.level(),
                updateAnalyticsMeanRecord.levelLot(),
                updateAnalyticsMeanRecord.mean());
        return ResponseEntity.status(204).build();
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

    private EntityModel<AnalyticsRecord> createEntityModel(AnalyticsRecord record, Pageable pageable) {
        return EntityModel.of(record,
                linkTo(methodOn(getClass()).getAnalyticsById(record.id())).withSelfRel());
    }

    private CollectionModel<EntityModel<AnalyticsRecord>> addPaginationLinks(
            CollectionModel<EntityModel<AnalyticsRecord>> collectionModel,
            Page<AnalyticsRecord> page,
            Pageable pageable) {

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();

        // Clear any existing collection-level links to prevent duplication
        collectionModel.removeLinks();

        // Link for the first page
        collectionModel.add(Link.of(uriBuilder
                        .replaceQueryParam("page", 0)
                        .replaceQueryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("first"));

        // Link for the previous page if it exists
        if (page.hasPrevious()) {
            collectionModel.add(Link.of(uriBuilder
                            .replaceQueryParam("page", pageable.getPageNumber() - 1)
                            .replaceQueryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("prev"));
        }

        // Link for the next page if it exists
        if (page.hasNext()) {
            collectionModel.add(Link.of(uriBuilder
                            .replaceQueryParam("page", pageable.getPageNumber() + 1)
                            .replaceQueryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("next"));
        }

        // Link for the last page
        collectionModel.add(Link.of(uriBuilder
                        .replaceQueryParam("page", page.getTotalPages() - 1)
                        .replaceQueryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("last"));

        // Add metadata about the current page
        collectionModel.add(Link.of(uriBuilder
                        .replaceQueryParam("page", pageable.getPageNumber())
                        .replaceQueryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("current-page"));

        return collectionModel;
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
    public abstract ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsDateBetween(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate);


    @GetMapping("/name-and-level")
    public abstract ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsByNameAndLevel(
            Pageable pageable, @RequestParam String name, @RequestParam String level);

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
