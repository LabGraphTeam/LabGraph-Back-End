package leonardo.labutilities.qualitylabpro.configs.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

	private final SecurityFilter securityFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(req -> {
					// Public endpoints
					req.requestMatchers(HttpMethod.POST, "/users/sign-in").permitAll();
					req.requestMatchers(HttpMethod.POST, "/users/sign-up").permitAll();
					req.requestMatchers(HttpMethod.POST, "/hematology-analytics/**").permitAll();

					// Swagger/OpenAPI endpoints
					req.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
							.permitAll();

					// Admin-only endpoints
					req.requestMatchers(HttpMethod.DELETE, "/generic-analytics/**")
							.hasRole("ADMIN");
					req.requestMatchers(HttpMethod.DELETE, "/biochemistry-analytics/**")
							.hasRole("ADMIN");
					req.requestMatchers(HttpMethod.DELETE, "/hematology-analytics/**")
							.hasRole("ADMIN");
					req.requestMatchers(HttpMethod.DELETE, "/coagulation-analytics/**")
							.hasRole("ADMIN");
					req.requestMatchers(HttpMethod.DELETE, "/users/**");

					// All other endpoints require authentication
					req.anyRequest().permitAll();
				}).addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public AuthenticationManager authMenager(AuthenticationConfiguration configuration)
			throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
