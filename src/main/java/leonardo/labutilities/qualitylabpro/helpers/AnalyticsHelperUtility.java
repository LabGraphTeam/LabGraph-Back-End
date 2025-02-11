package leonardo.labutilities.qualitylabpro.helpers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import leonardo.labutilities.qualitylabpro.controllers.analytics.AnalyticsHelperController;
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.AnalyticsDTO;

public class AnalyticsHelperUtility {

	private AnalyticsHelperUtility() {}

	public static EntityModel<AnalyticsDTO> createEntityModel(AnalyticsDTO analyticsRecord,
			Pageable pageable, AnalyticsHelperController controller) {
		return EntityModel.of(analyticsRecord, Link.of(ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/backend")
				.path(linkTo(methodOn(controller.getClass()).getAnalyticsById(analyticsRecord.id()))
						.toUri().getPath())
				.toUriString()).withSelfRel());
	}

	public static CollectionModel<EntityModel<AnalyticsDTO>> addPaginationLinks(
			CollectionModel<EntityModel<AnalyticsDTO>> collectionModel, Page<AnalyticsDTO> page,
			Pageable pageable) {

		UriComponentsBuilder uriBuilder =
				ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/backend-api"
						+ ServletUriComponentsBuilder.fromCurrentRequest().build().getPath());

		collectionModel.add(Link.of(uriBuilder.replaceQueryParam("page", 0)
				.replaceQueryParam("size", pageable.getPageSize()).toUriString()
				.replace("%2520", "%20")).withRel("first"));

		if (page.hasPrevious()) {
			collectionModel
					.add(Link.of(uriBuilder.replaceQueryParam("page", pageable.getPageNumber() - 1)
							.replaceQueryParam("size", pageable.getPageSize()).toUriString()
							.replace("%2520", "%20")).withRel("prev"));
		}

		if (page.hasNext()) {
			collectionModel
					.add(Link.of(uriBuilder.replaceQueryParam("page", pageable.getPageNumber() + 1)
							.replaceQueryParam("size", pageable.getPageSize()).toUriString()
							.replace("%2520", "%20")).withRel("next"));
		}

		collectionModel.add(Link.of(uriBuilder.replaceQueryParam("page", page.getTotalPages() - 1)
				.replaceQueryParam("size", pageable.getPageSize()).toUriString()
				.replace("%2520", "%20")).withRel("last"));

		collectionModel.add(Link.of(uriBuilder.replaceQueryParam("page", pageable.getPageNumber())
				.replaceQueryParam("size", pageable.getPageSize()).toUriString()
				.replace("%2520", "%20")).withRel("current-page"));

		collectionModel.add(Link.of(String.valueOf(page.getTotalPages()), "totalPages"));
		collectionModel.add(Link.of(String.valueOf(page.getNumber()), "currentPage"));

		return collectionModel;
	}
}
