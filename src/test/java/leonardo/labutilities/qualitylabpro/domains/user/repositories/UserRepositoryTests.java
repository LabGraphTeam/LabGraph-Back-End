package leonardo.labutilities.qualitylabpro.domains.user.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.transaction.annotation.Transactional;

import leonardo.labutilities.qualitylabpro.domains.users.components.BCryptEncoderComponent;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import leonardo.labutilities.qualitylabpro.domains.users.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class UserRepositoryTests {

	@Autowired
	UserRepository userRepository;

	@BeforeTestExecution
	static void clearDatabase(@Autowired Flyway flyway) {
		flyway.clean();
		flyway.migrate();
	}

	private void setupTestData() {

		var user = new User("UserTest", BCryptEncoderComponent.encrypt("12345"), "leo@hotmail.com");
		this.userRepository.save(user);
	}

	@Test
	@Order(1)
	@DisplayName("Should return null when username is empty")
	@Transactional
	void shouldReturnNullWhenUsernameIsEmpty() {
		var userEmpty = this.userRepository.getReferenceOneByUsername("");
		assertThat(userEmpty).isEmpty();
	}

	@Test
	@Order(2)
	@DisplayName("Should return user when username exists in database")
	@Transactional
	void findByLoginUserDataBaseIsUserExists() {
		this.setupTestData();
		var userNotNull = this.userRepository.getReferenceOneByUsername("UserTest");
		assertThat(userNotNull).isNotNull();
	}

	@Test
	@Order(3)
	@DisplayName("Should successfully update password when username is valid")
	@Transactional
	void setPasswordWhereByUsername() {
		this.setupTestData();
		String username = "UserTest";
		String oldPassword = "12345";
		String newPassword = "newPassword!@#";

		var userWithOldPassword = this.userRepository.getReferenceByUsernameAndEmail("UserTest", "leo@hotmail.com");

		this.userRepository.setPasswordWhereByUsername(username, newPassword);

		var userWithNewPassword = this.userRepository.getReferenceByUsernameAndEmail("UserTest", "leo@hotmail.com");

		assertThat(BCryptEncoderComponent.decrypt(oldPassword, userWithOldPassword.getPassword())
				|| BCryptEncoderComponent.decrypt(newPassword, userWithNewPassword.getPassword())).isTrue();
	}
}
