package leonardo.labutilities.qualitylabpro.controllers.analytics;

import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.dtos.analytics.GroupedMeanAndStdRecordByLevel;
import leonardo.labutilities.qualitylabpro.dtos.analytics.GroupedResultsByLevel;
import leonardo.labutilities.qualitylabpro.dtos.analytics.UpdateAnalyticsMeanRecord;
import leonardo.labutilities.qualitylabpro.services.analytics.AnalyticsHelperService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AnalyticsHelperController {
    private final AnalyticsHelperService analyticsHelperService;

    public AnalyticsHelperController(AnalyticsHelperService analyticsHelperService) {
        this.analyticsHelperService = analyticsHelperService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsRecord> getAnalyticsById(@PathVariable Long id) {
        return ResponseEntity.ok(analyticsHelperService.findOneById(id));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAnalyticsResultById(@PathVariable Long id) {
        analyticsHelperService.deleteAnalyticsById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<List<AnalyticsRecord>> postAnalytics(
            @Valid @RequestBody List<@Valid AnalyticsRecord> values) {
        analyticsHelperService.saveNewAnalyticsRecords(values);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping()
    public ResponseEntity<Void> updateAnalyticsMean(
            @Valid @RequestBody UpdateAnalyticsMeanRecord updateAnalyticsMeanRecord) {
        analyticsHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(
                updateAnalyticsMeanRecord.name(), updateAnalyticsMeanRecord.level(),
                updateAnalyticsMeanRecord.levelLot(), updateAnalyticsMeanRecord.mean());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grouped-by-level")
    public ResponseEntity<List<GroupedResultsByLevel>> getGroupedByLevel(@RequestParam String name,
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

    public ResponseEntity<CollectionModel<EntityModel<AnalyticsRecord>>> getAllAnalyticsWithLinks(
            List<String> names, Pageable pageable) {
        Page<AnalyticsRecord> resultsList =
                analyticsHelperService.findAnalyticsPagedByNameIn(names, pageable);

        var entityModels = resultsList.getContent().stream()
                                      .map(record -> createEntityModel(record, pageable)).collect(Collectors.toList());

        var result = addPaginationLinks(CollectionModel.of(entityModels), resultsList, pageable);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<CollectionModel<EntityModel<AnalyticsRecord>>> getAnalyticsByDateBetweenWithLinks(
            List<String> names, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        Page<AnalyticsRecord> analyticsRecordPaged = analyticsHelperService
                .findAnalyticsByNameInAndDateBetweenWithLinks(names, startDate, endDate, pageable);

        if (analyticsRecordPaged == null) {
            return ResponseEntity.noContent().build();
        }

        var entityModels = analyticsRecordPaged.getContent().stream()
                                               .map(record -> createEntityModel(record, pageable)).collect(Collectors.toList());

        var collectionModel = CollectionModel.of(entityModels);
        var result = addPaginationLinks(collectionModel, analyticsRecordPaged, pageable);

        return ResponseEntity.ok(result);
    }

    EntityModel<AnalyticsRecord> createEntityModel(AnalyticsRecord record, Pageable pageable) {
        return EntityModel.of(record, Link.of(ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                         .path("/backend-api")
                                                                         .path(linkTo(methodOn(getClass()).getAnalyticsById(record.id())).toUri().getPath())
                                                                         .toUriString()).withSelfRel());
    }

    private CollectionModel<EntityModel<AnalyticsRecord>> addPaginationLinks(
            CollectionModel<EntityModel<AnalyticsRecord>> collectionModel,
            Page<AnalyticsRecord> page, Pageable pageable) {

        UriComponentsBuilder uriBuilder =
                ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/backend-api"
                                                                             +
                                                                             ServletUriComponentsBuilder.fromCurrentRequest().build().getPath());

        // Clear any existing collection-level links to prevent duplication
        // collectionModel.removeLinks();

        // Link for the first page
        collectionModel.add(Link.of(uriBuilder.replaceQueryParam("page", 0)
                                              .replaceQueryParam("size", pageable.getPageSize()).toUriString()
                                              .replace("%2520", "%20")).withRel("first"));

        // Link for the previous page if it exists
        if (page.hasPrevious()) {
            collectionModel
                    .add(Link.of(uriBuilder.replaceQueryParam("page", pageable.getPageNumber() - 1)
                                           .replaceQueryParam("size", pageable.getPageSize()).toUriString()
                                           .replace("%2520", "%20")).withRel("prev"));
        }

        // Link for the next page if it exists
        if (page.hasNext()) {
            collectionModel
                    .add(Link.of(uriBuilder.replaceQueryParam("page", pageable.getPageNumber() + 1)
                                           .replaceQueryParam("size", pageable.getPageSize()).toUriString()
                                           .replace("%2520", "%20")).withRel("next"));
        }

        // Link for the last page
        collectionModel.add(Link.of(uriBuilder.replaceQueryParam("page", page.getTotalPages() - 1)
                                              .replaceQueryParam("size", pageable.getPageSize()).toUriString()
                                              .replace("%2520", "%20")).withRel("last"));

        // Add metadata about the current page
        collectionModel.add(Link.of(uriBuilder.replaceQueryParam("page", pageable.getPageNumber())
                                              .replaceQueryParam("size", pageable.getPageSize()).toUriString()
                                              .replace("%2520", "%20")).withRel("current-page"));

        // collectionModel.add(Link.of(String.valueOf(page.getTotalPages())).withSelfRel());
        collectionModel.add(Link.of(String.valueOf(page.getTotalPages()), "totalPages"));
        collectionModel.add(Link.of(String.valueOf(page.getNumber()), "currentPage"));


        return collectionModel;
    }
}
