package leonardo.labutilities.qualitylabpro.domains.shared.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import leonardo.labutilities.qualitylabpro.domains.users.repositories.UserRepository;

@ExtendWith(SpringExtension.class)
class AuthenticationServiceTests {

	@InjectMocks
	private AuthenticationService authenticationService;

	@Mock
	private UserRepository userRepository;

	@Test
	void loadUserByUsername_ExistingUser_ReturnsUserDetails() {
		// Arrange
		String username = "testuser";
		User expectedUser = new User();
		expectedUser.setUsername(username);

		when(this.userRepository.getReferenceOneByUsername(username)).thenReturn(Optional.of(expectedUser));

		// Act
		var userDetails = this.authenticationService.loadUserByUsername(username);

		// Assert
		assertNotNull(userDetails);
		assertEquals(username, userDetails.getUsername());
		verify(this.userRepository, times(1)).getReferenceOneByUsername(username);
	}

	@Test
	void loadUserByUsername_UserNotFound_ThrowsException() {
		// Arrange
		String username = "nonexistentuser";
		when(this.userRepository.getReferenceOneByUsername(username)).thenReturn(Optional.empty());

		// Act & Assert
		var exception = assertThrows(UsernameNotFoundException.class,
				() -> this.authenticationService.loadUserByUsername(username));

		assertEquals("User not found: " + username, exception.getMessage());
		verify(this.userRepository, times(1)).getReferenceOneByUsername(username);
	}
}
