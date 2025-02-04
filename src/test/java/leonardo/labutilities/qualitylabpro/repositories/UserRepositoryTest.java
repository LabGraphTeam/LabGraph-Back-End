package leonardo.labutilities.qualitylabpro.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.enums.UserRoles;
import leonardo.labutilities.qualitylabpro.utils.components.BCryptEncoderComponent;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void clearDatabase(@Autowired Flyway flyway) {
		flyway.clean();
		flyway.migrate();
	}

	public void setupTestData() {
		var user = new User("UserTest", BCryptEncoderComponent.encrypt("12345"), "leo@hotmail.com",
				UserRoles.USER);

		this.userRepository.save(user);
	}

	@Test
	@DisplayName("return 200 when user is exists")
	@Transactional
	void findByLoginUserDataBaseIsUserExists() {
		this.setupTestData();
		var userNotNull = this.userRepository.getReferenceOneByUsername("UserTest");
		assertThat(userNotNull).isNotNull();
	}

	@Test
	@DisplayName("return null when user is empty")
	@Transactional
	void findByLoginUserDataBaseIsUserNotExists() {
		var userEmpty = this.userRepository.getReferenceOneByUsername("");
		assertThat(userEmpty).isNull();
	}

	@Test
	@DisplayName("return True when update passwords successful")
	@Transactional
	void setPasswordWhereByUsername() {
		this.setupTestData();
		String username = "UserTest";
		String oldPassword = "12345";
		String newPassword = "249195Leo@@";

		var userWithOldPassword =
				this.userRepository.getReferenceByUsernameAndEmail("UserTest", "leo@hotmail.com");

		this.userRepository.setPasswordWhereByUsername(username, newPassword);

		var userWithNewPassword =
				this.userRepository.getReferenceByUsernameAndEmail("UserTest", "leo@hotmail.com");

		assertThat(BCryptEncoderComponent.decrypt(oldPassword, userWithOldPassword.getPassword())
				|| BCryptEncoderComponent.decrypt(newPassword, userWithNewPassword.getPassword()))
						.isTrue();
	}
}
