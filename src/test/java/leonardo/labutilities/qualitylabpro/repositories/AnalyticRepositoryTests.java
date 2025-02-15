package leonardo.labutilities.qualitylabpro.repositories;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Transient;
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.entities.Analytic;
import leonardo.labutilities.qualitylabpro.utils.components.RulesValidatorComponent;
import leonardo.labutilities.qualitylabpro.utils.constants.AvailableAnalyticsNames;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticMapper;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AnalyticRepositoryTests {
	private static final List<String> ANALYTICS_NAME_LIST = AvailableAnalyticsNames.ALL_ANALYTICS;
	@Transient
	static Flyway flyway;
	@Transient
	final RulesValidatorComponent rulesValidatorComponent = new RulesValidatorComponent();
	@Transient
	final LocalDateTime testDate = LocalDateTime.of(2024, 12, 16, 7, 53);
	@Autowired
	AnalyticsRepository repository;
	@Autowired
	private EntityManager entityManager;

	@BeforeAll
	static void setupDatabase(@Autowired Flyway flyway) {
		flyway.clean();
		flyway.migrate();
	}

	@BeforeEach
	void setupTestData() {
		AnalyticsDTO analyticsRecord = createSampleRecord();

		Analytic analytic = new Analytic();
		analytic.setDate(analyticsRecord.date());
		analytic.setLevelLot(analyticsRecord.level_lot());
		analytic.setTestLot(analyticsRecord.test_lot());
		analytic.setName(analyticsRecord.name());
		analytic.setLevel(analyticsRecord.level());
		analytic.setValue(analyticsRecord.value());
		analytic.setMean(analyticsRecord.mean());
		analytic.setSd(analyticsRecord.sd());
		analytic.setUnitValue(analyticsRecord.unit_value());
		analytic.setRules(analyticsRecord.rules());
		analytic.setDescription(analyticsRecord.description());


		this.repository.saveAndFlush(analytic); // Use saveAndFlush instead of just save
		this.entityManager.clear(); // Clear again to ensure fresh state
	}

	@Test()
	@DisplayName("Should return analytics when searching by level within date range")
	void testFindAllByLevel() {
		PageRequest pageable = PageRequest.of(0, 10);
		List<AnalyticsDTO> results = this.repository
				.findByNameInAndLevelAndDateBetween(ANALYTICS_NAME_LIST, "PCCC1",
						this.testDate.minusDays(1), this.testDate.plusDays(1), pageable)
				.stream().toList();
		assertThat(results).as("Results should not be empty for the given date range").isNotEmpty();
		assertThat(results.getFirst().level()).as("Level should match PCCC1").isEqualTo("PCCC1");
	}

	@Test
	@DisplayName("Should update analytic mean value when searching by name, level and levelLot")
	void testUpdateLevelLotByNameAndLevelAndLevelLot() {
		this.repository.updateMeanByNameAndLevelAndLevelLot("ALB2", "PCCC1", "0774693", 3.25);
		this.entityManager.clear();
		Analytic analytic = this.repository
				.findByNameAndLevelAndLevelLot(PageRequest.of(0, 10), "ALB2", "PCCC1", "0774693")
				.get(0);
		System.out.println(analytic.getMean());
		assertThat(analytic.getMean()).isEqualTo(3.25);
	}

	@Test
	@DisplayName("Should return true when analytic exists by name and false when it doesn't")
	void testExistsByName() {
		assertTrue(this.repository.existsByName("ALB2"));
		assertFalse(this.repository.existsByName("NONEXISTENT"));
	}

	@Test
	@DisplayName("Should return paginated analytics when searching by name")
	void testFindAllByName() {
		PageRequest pageable = PageRequest.of(0, 10);
		List<AnalyticsDTO> results = this.repository.findByName("ALB2", pageable).stream()
				.map(AnalyticMapper::toRecord).toList();
		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
	}

	@Test
	@DisplayName("Should return true when analytic exists by date, level and name combination")
	void testExistsByDateAndLevelAndName() {
		boolean exists =
				this.repository.existsByDateAndLevelAndName(this.testDate, "PCCC1", "ALB2");

		assertTrue(exists);
	}

	@Test
	@DisplayName("Should return paginated analytics when searching by name and level")
	void testFindAllByNameAndLevel() {
		PageRequest pageable = PageRequest.of(0, 10);

		List<AnalyticsDTO> results = this.repository.findByNameAndLevel(pageable, "ALB2", "PCCC1")
				.stream().map(AnalyticMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
		assertThat(results.getFirst().level()).isEqualTo("PCCC1");
	}

	@Test
	@DisplayName("Should return analytics when searching by names list and date range")
	void testFindAllByNameInAndDateBetween() {
		List<String> names = List.of("ALB2");

		Page<AnalyticsDTO> results = this.repository.findByNameInAndDateBetween(names,
				this.testDate.minusDays(1), this.testDate.plusDays(1), Pageable.unpaged());

		assertThat(results).isNotEmpty();
		assertThat(results.getContent().get(0).name()).isEqualTo("ALB2");
	}

	@Test
	@DisplayName("Should return paginated analytics when searching by names list")
	void testFindAllByNameIn() {
		List<String> names = List.of("ALB2");
		PageRequest pageable = PageRequest.of(0, 10);

		List<AnalyticsDTO> results = this.repository.findByNameIn(names, pageable).stream()
				.map(AnalyticMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
	}

	@Test
	@DisplayName("Should return paginated analytics when searching by name, level and date range")
	void testFindAllByNameAndLevelAndDateBetween() {
		PageRequest pageable = PageRequest.of(0, 10);

		List<AnalyticsDTO> results = this.repository
				.findByNameAndLevelAndDateBetween("ALB2", "PCCC1", this.testDate.minusDays(1),
						this.testDate.plusDays(1), pageable)
				.stream().map(AnalyticMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
		assertThat(results.getFirst().level()).isEqualTo("PCCC1");
	}

	@Test
	@DisplayName("Should return all analytics when searching within date range")
	void testFindAllByDateBetween() {
		List<AnalyticsDTO> results = this.repository
				.findByDateBetween(this.testDate.minusDays(1), this.testDate.plusDays(1)).stream()
				.map(AnalyticMapper::toRecord).toList();
		assertThat(results).isNotEmpty();
	}

	@Test
	@DisplayName("Should return analytics grouped by level when searching by name and date range")
	void testFindAllByNameAndDateBetweenGroupByLevel() {
		PageRequest pageable = PageRequest.of(0, 10);

		List<AnalyticsDTO> results = this.repository
				.findByNameAndDateBetweenGroupByLevel("ALB2", this.testDate.minusDays(1),
						this.testDate.plusDays(1), pageable)
				.stream().map(AnalyticMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
	}
}
