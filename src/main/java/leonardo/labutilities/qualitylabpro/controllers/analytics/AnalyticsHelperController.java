package leonardo.labutilities.qualitylabpro.controllers.analytics;

import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.UpdateAnalyticsMeanDTO;
import leonardo.labutilities.qualitylabpro.services.analytics.AbstractAnalyticHelperService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
	private final AbstractAnalyticHelperService analyticHelperService;

	public AnalyticsHelperController(AbstractAnalyticHelperService analyticHelperService) {
		this.analyticHelperService = analyticHelperService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<AnalyticsDTO> getAnalyticsById(@PathVariable Long id) {
		return ResponseEntity.ok(analyticHelperService.findOneById(id));
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deleteAnalyticsResultById(@PathVariable Long id) {
		analyticHelperService.deleteAnalyticsById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	@Transactional
	public ResponseEntity<List<AnalyticsDTO>> postAnalytics(
			@Valid @RequestBody List<@Valid AnalyticsDTO> values) {
		analyticHelperService.saveNewAnalyticsRecords(values);
		return ResponseEntity.status(201).build();
	}

	@PatchMapping()
	public ResponseEntity<Void> updateAnalyticsMean(
			@Valid @RequestBody UpdateAnalyticsMeanDTO updateAnalyticsMeanDTO) {
		analyticHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(
				updateAnalyticsMeanDTO.name(), updateAnalyticsMeanDTO.level(),
				updateAnalyticsMeanDTO.levelLot(), updateAnalyticsMeanDTO.mean());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/grouped-by-level")
	public ResponseEntity<List<GroupedResultsByLevelDTO>> getGroupedByLevel(
			@RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		List<GroupedResultsByLevelDTO> groupedData = analyticHelperService
				.findAnalyticsWithGroupedResults(name, startDate, endDate, pageable);
		return ResponseEntity.ok(groupedData);
	}

	@GetMapping("/grouped-by-level/mean-deviation")
	public ResponseEntity<List<GroupedMeanAndStdByLevelDTO>> getMeanAndDeviationGrouped(
			@RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		List<GroupedMeanAndStdByLevelDTO> groupedData = analyticHelperService
				.calculateGroupedMeanAndStandardDeviation(name, startDate, endDate, pageable);
		return ResponseEntity.ok(groupedData);
	}

	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalyticsWithLinks(
			List<String> names, @PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		Page<AnalyticsDTO> resultsList =
				analyticHelperService.findAnalyticsPagedByNameIn(names, pageable);

		var entityModels = resultsList.getContent().stream()
				.map(record -> createEntityModel(record, pageable)).collect(Collectors.toList());

		var result = addPaginationLinks(CollectionModel.of(entityModels), resultsList, pageable);
		return ResponseEntity.ok(result);
	}

	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAnalyticsByDateBetweenWithLinks(
			List<String> names, LocalDateTime startDate, LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		Page<AnalyticsDTO> analyticsRecordPaged = analyticHelperService
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

	EntityModel<AnalyticsDTO> createEntityModel(AnalyticsDTO record, Pageable pageable) {
		return EntityModel.of(record, Link.of(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/backend-api")
				.path(linkTo(methodOn(getClass()).getAnalyticsById(record.id())).toUri().getPath())
				.toUriString()).withSelfRel());
	}

	private CollectionModel<EntityModel<AnalyticsDTO>> addPaginationLinks(
			CollectionModel<EntityModel<AnalyticsDTO>> collectionModel, Page<AnalyticsDTO> page,
			Pageable pageable) {

		UriComponentsBuilder uriBuilder =
				ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/backend-api"
						+ ServletUriComponentsBuilder.fromCurrentRequest().build().getPath());

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

		collectionModel.add(Link.of(String.valueOf(page.getTotalPages()), "totalPages"));
		collectionModel.add(Link.of(String.valueOf(page.getNumber()), "currentPage"));


		return collectionModel;
	}
}
