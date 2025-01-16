package leonardo.labutilities.qualitylabpro.repositories;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Transient;
import leonardo.labutilities.qualitylabpro.constants.AvailableBiochemistryAnalytics;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;

import leonardo.labutilities.qualitylabpro.entities.Analytics;
import leonardo.labutilities.qualitylabpro.utils.components.RulesValidatorComponent;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticsMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AnalyticsRepositoryTest {
	@Autowired
	AnalyticsRepository repository;
	@Transient
    final
    RulesValidatorComponent rulesValidatorComponent = new RulesValidatorComponent();
	@Transient
	static Flyway flyway;
	@Transient
    final LocalDateTime testDate = LocalDateTime.of(2024, 12, 16, 7, 53);
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private static final List<String> ANALYTICS_NAME_LIST = new AvailableBiochemistryAnalytics().availableBioAnalytics();


	@BeforeAll
	static void setupDatabase(@Autowired Flyway flyway) {
		flyway.clean();
		flyway.migrate();
	}
	@BeforeEach
	void setupTestData() {
		Analytics analytics = new Analytics(createSampleRecord(), rulesValidatorComponent);
		repository.save(analytics);
	}

	@Test()
	@DisplayName("Should find all Analytics by level")
	void testFindAllByLevel() {
		PageRequest pageable = PageRequest.of(0, 10);
		List<AnalyticsRecord> results = repository
				.findByNameInAndLevelAndDateBetween(ANALYTICS_NAME_LIST,
						"PCCC1",testDate.minusDays(1), testDate.plusDays(1), pageable)
				.stream().map(AnalyticsMapper::toRecord).toList();
		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().level()).isEqualTo("PCCC1");
	}

	@Test
	@DisplayName("Should update Analytics.LevelLot by name,level and levelLot and return void")
	void testUpdateLevelLotByNameAndLevelAndLevelLot() {
		repository.updateMeanByNameAndLevelAndLevelLot("ALB2", "PCCC1", "0774693", 3.25);
		entityManager.clear();
		Analytics analytics = repository
				.findByNameAndLevelAndLevelLot
						(PageRequest.of(0, 10), "ALB2", "PCCC1", "0774693").get(0);
		System.out.println(analytics.getMean());
		assertThat(analytics.getMean()).isEqualTo(3.25);
	}
	@Test
	@DisplayName("Should find analytics by name when exists")
	void testExistsByName() {
		assertTrue(repository.existsByName("ALB2"));
		assertFalse(repository.existsByName("NONEXISTENT"));
	}

	@Test
	@DisplayName("Should find all analytics by name with pagination")
	void testFindAllByName() {
		PageRequest pageable = PageRequest.of(0, 10);
		List<AnalyticsRecord> results = repository.findByName("ALB2", pageable)
				.stream().map(AnalyticsMapper::toRecord).toList();
		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
	}

	@Test
	@DisplayName("Should verify existence by date, level and name")
	void testExistsByDateAndLevelAndName() {
		boolean exists =
				repository.existsByDateAndLevelAndName(testDate, "PCCC1", "ALB2");

		assertTrue(exists);
	}

	@Test
	@DisplayName("Should find all by name and level with pagination")
	void testFindAllByNameAndLevel() {
		PageRequest pageable = PageRequest.of(0, 10);

		List<AnalyticsRecord> results =
				repository.findByNameAndLevel(pageable, "ALB2", "PCCC1")
						.stream().map(AnalyticsMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
		assertThat(results.getFirst().level()).isEqualTo("PCCC1");
	}

	@Test
	@DisplayName("Should find all by names in list and date range")
	void testFindAllByNameInAndDateBetween() {
		setupTestData();
		List<String> names = List.of("ALB2");

		List<AnalyticsRecord> results = repository.findByNameInAndDateBetween(names,
				testDate.minusDays(1), testDate.plusDays(1)).stream().map(AnalyticsMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
	}

	@Test
	@DisplayName("Should find all by names in list with pagination")
	void testFindAllByNameIn() {
		setupTestData();
		List<String> names = List.of("ALB2");
		PageRequest pageable = PageRequest.of(0, 10);

		List<AnalyticsRecord> results = repository.findByNameIn(names, pageable)
				.stream().map(AnalyticsMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
	}

	@Test
	@DisplayName("Should find all by name, level and date range with pagination")
	void testFindAllByNameAndLevelAndDateBetween() {
		setupTestData();
		PageRequest pageable = PageRequest.of(0, 10);

		List<AnalyticsRecord> results = repository.findByNameAndLevelAndDateBetween(
				"ALB2", "PCCC1", testDate.minusDays(1), testDate.plusDays(1), pageable)
				.stream().map(AnalyticsMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
		assertThat(results.getFirst().level()).isEqualTo("PCCC1");
	}

	@Test
	@DisplayName("Should find all by date range")
	void testFindAllByDateBetween() {
		setupTestData();
		List<AnalyticsRecord> results =
				repository
						.findByDateBetween(testDate.minusDays(1), testDate.plusDays(1))
						.stream().map(AnalyticsMapper::toRecord).toList();
		assertThat(results).isNotEmpty();
	}

	@Test
	@DisplayName("Should find all by name and date range grouped by level")
	void testFindAllByNameAndDateBetweenGroupByLevel() {
		setupTestData();
		PageRequest pageable = PageRequest.of(0, 10);

		List<AnalyticsRecord> results = repository.findByNameAndDateBetweenGroupByLevel(
				"ALB2", testDate.minusDays(1), testDate.plusDays(1), pageable)
				.stream().map(AnalyticsMapper::toRecord).toList();

		assertThat(results).isNotEmpty();
		assertThat(results.getFirst().name()).isEqualTo("ALB2");
	}
}
