package leonardo.labutilities.qualitylabpro.domains.shared.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import leonardo.labutilities.qualitylabpro.domains.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthenticationService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = this.userRepository.getReferenceOneByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found: " + username);
		}
		return user;
	}
}
