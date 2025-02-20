package leonardo.labutilities.qualitylabpro.domains.user.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

class UserTests {

	private User user;

	@BeforeEach
	void setUp() {
		this.user = new User();
	}

	@Test
	void createUser_WithDefaultConstructor_ShouldCreateEmptyUser() {
		assertNotNull(this.user);
		assertNull(this.user.getId());
		assertNull(this.user.getEmail());
		assertNull(this.user.getPassword());
		assertTrue(this.user.isEnabled());
	}

	@Test
	void settersAndGetters_ShouldWorkProperly() {
		Long id = 1L;
		String name = "Test User";
		String email = "test@example.com";
		String password = "password123";

		this.user.setId(id);
		this.user.setUsername(name);
		this.user.setEmail(email);

		assertEquals(id, this.user.getId());
		assertEquals(name, this.user.getUsername());
		assertEquals(email, this.user.getEmail());
		assertNotEquals(password, this.user.getPassword());
		assertTrue(this.user.isEnabled());
	}

	@Test
	void userDetails_ShouldImplementUserDetailsInterface() {
		String email = "test@example.com";
		String userName = "Test User";

		this.user.setEmail(email);
		this.user.setUsername(userName);

		assertEquals(userName, this.user.getUsername());
		assertTrue(this.user.isAccountNonExpired());
		assertTrue(this.user.isAccountNonLocked());
		assertTrue(this.user.isCredentialsNonExpired());
		assertTrue(this.user.isEnabled());

		var expectedAuthority = new SimpleGrantedAuthority("ROLE_USER");
		assertTrue(this.user.getAuthorities().contains(expectedAuthority));
	}

	@Test
	void equalsAndHashCode_ShouldWorkProperly() {
		User user1 = new User();
		User user2 = new User();

		// Two null IDs should not be equal
		assertNotEquals(user1, user2);

		// Same object should be equal to itself
		assertEquals(user1, user1);

		// Objects with same non-null ID should be equal
		user1.setId(1L);
		user2.setId(1L);
		assertEquals(user1, user2);

		// Objects with different IDs should not be equal
		user2.setId(2L);
		assertNotEquals(user1, user2);

		// Null object comparison
		assertNotEquals(null, user1);

		// Different type comparison
		assertNotEquals(user1, new Object());
	}

	@Test
	void auditFields_ShouldBeInitializedCorrectly() {
		assertNull(this.user.getCreatedAt());
		assertNull(this.user.getUpdatedAt());

		LocalDateTime now = LocalDateTime.now();
		this.user.setCreatedAt(now);
		this.user.setUpdatedAt(now);

		assertEquals(now, this.user.getCreatedAt());
		assertEquals(now, this.user.getUpdatedAt());
	}

	@Test
	void toString_ShouldReturnUserDetails() {
		this.user.setId(1L);
		this.user.setUsername("Test User");
		this.user.setEmail("test@example.com");

		String result = this.user.toString();

		assertTrue(result.contains("Test User"));
		assertTrue(result.contains("test@example.com"));
		assertTrue(result.contains("1"));
	}
}
