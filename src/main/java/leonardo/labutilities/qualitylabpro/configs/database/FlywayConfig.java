package leonardo.labutilities.qualitylabpro.configs.database;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "local"})
public class FlywayConfig {

	@Bean
	FlywayMigrationStrategy repairFlywayStrategy() {
		return flyway -> {
			flyway.repair();
			flyway.migrate();
		};
	}
}
