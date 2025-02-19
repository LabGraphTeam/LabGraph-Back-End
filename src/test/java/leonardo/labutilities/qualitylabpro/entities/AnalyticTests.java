package leonardo.labutilities.qualitylabpro.entities;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticMapper;

class AnalyticTests {

	private Analytic analytic;
	private AnalyticsDTO analyticsDTO;
	private User user;

	@BeforeEach
	void setUp() {
		this.user = new User();
		this.user.setId(1L);
		var analyticEntity = AnalyticMapper.toEntity(createSampleRecord());

		this.analytic = analyticEntity;
		this.analyticsDTO = new AnalyticsDTO(this.analytic);
	}

	@Test
	void createAnalytic_WithDefaultConstructor_ShouldCreateEmptyAnalytic() {
		this.analytic = new Analytic();

		assertNotNull(this.analytic);
		assertNull(this.analytic.getId());
		assertNull(this.analytic.getName());
	}

	@Test
	void createAnalytic_WithDTO_ShouldMapAllProperties() {
		this.analytic = new Analytic(this.analyticsDTO);

		assertEquals(this.analyticsDTO.name(), this.analytic.getName());
		assertEquals(this.analyticsDTO.level(), this.analytic.getLevel());
		assertEquals(this.analyticsDTO.value(), this.analytic.getValue());
		assertEquals(this.analyticsDTO.mean(), this.analytic.getMean());
		assertEquals(this.analyticsDTO.sd(), this.analytic.getSd());
		assertEquals(this.analyticsDTO.unit_value(), this.analytic.getUnitValue());
		assertEquals(this.analyticsDTO.date(), this.analytic.getDate());
		assertEquals(this.analyticsDTO.level_lot(), this.analytic.getLevelLot());
		assertEquals(this.analyticsDTO.test_lot(), this.analytic.getTestLot());
		assertEquals(this.analyticsDTO.rules(), this.analytic.getRules());
		assertEquals(this.analyticsDTO.description(), this.analytic.getDescription());
	}

	@Test
	void settersAndGetters_ShouldWorkProperly() {
		this.analytic = new Analytic();

		this.analytic.setId(1L);
		this.analytic.setUser(this.user);
		this.analytic.setName("TEST");
		this.analytic.setLevel("LEVEL1");
		this.analytic.setValue(10.0);

		assertEquals(1L, this.analytic.getId());
		assertEquals(this.user, this.analytic.getUser());
		assertEquals("TEST", this.analytic.getName());
		assertEquals("LEVEL1", this.analytic.getLevel());
		assertEquals(10.0, this.analytic.getValue());
	}

	@Test
	void createdAtAndUpdatedAt_ShouldBeAutomaticallySet() {
		this.analytic = new Analytic();

		assertNull(this.analytic.getCreatedAt());
		assertNull(this.analytic.getUpdatedAt());

		// Note: Actual timestamp setting would be tested in integration tests
		// as it requires database interaction
	}

	@Test
	void validatedBy_ShouldBeNullByDefault() {
		this.analytic = new Analytic();

		assertNull(this.analytic.getValidatedBy());

		User validator = new User();
		this.analytic.setValidatedBy(validator);

		assertEquals(validator, this.analytic.getValidatedBy());
	}
}
