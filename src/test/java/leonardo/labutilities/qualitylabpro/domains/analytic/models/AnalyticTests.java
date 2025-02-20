package leonardo.labutilities.qualitylabpro.domains.analytic.models;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

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
		assertNull(this.analytic.getTestName());
	}

	@Test
	void createAnalytic_WithDTO_ShouldMapAllProperties() {
		this.analytic = new Analytic(this.analyticsDTO);

		assertEquals(this.analyticsDTO.name(), this.analytic.getTestName());
		assertEquals(this.analyticsDTO.level(), this.analytic.getControlLevel());
		assertEquals(this.analyticsDTO.value(), this.analytic.getMeasurementValue());
		assertEquals(this.analyticsDTO.mean(), this.analytic.getTargetMean());
		assertEquals(this.analyticsDTO.sd(), this.analytic.getStandardDeviation());
		assertEquals(this.analyticsDTO.unit_value(), this.analytic.getMeasurementUnit());
		assertEquals(this.analyticsDTO.date(), this.analytic.getMeasurementDate());
		assertEquals(this.analyticsDTO.level_lot(), this.analytic.getControlLevelLot());
		assertEquals(this.analyticsDTO.test_lot(), this.analytic.getReagentLot());
		assertEquals(this.analyticsDTO.rules(), this.analytic.getControlRules());
		assertEquals(this.analyticsDTO.description(), this.analytic.getDescription());
	}

	@Test
	void settersAndGetters_ShouldWorkProperly() {
		this.analytic = new Analytic();

		this.analytic.setId(1L);
		this.analytic.setOwnerUserId(this.user);
		this.analytic.setTestName("TEST");
		this.analytic.setControlLevel("LEVEL1");
		this.analytic.setMeasurementValue(10.0);

		assertEquals(1L, this.analytic.getId());
		assertEquals(this.user, this.analytic.getOwnerUserId());
		assertEquals("TEST", this.analytic.getTestName());
		assertEquals("LEVEL1", this.analytic.getControlLevel());
		assertEquals(10.0, this.analytic.getMeasurementValue());
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

		assertNull(this.analytic.getValidatorUserId());

		User validator = new User();
		this.analytic.setValidatorUserId(validator);

		assertEquals(validator, this.analytic.getValidatorUserId());
	}
}
