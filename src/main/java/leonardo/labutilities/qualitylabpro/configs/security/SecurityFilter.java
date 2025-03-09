package leonardo.labutilities.qualitylabpro.configs.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.TokenService;
import leonardo.labutilities.qualitylabpro.domains.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws IOException {
        try {
            log.info("Starting security filter for request to: {}", request.getRequestURI());
            var tokenJWT = this.getToken(request);
            if (tokenJWT != null) {
                log.debug("JWT token found in request");
                var subject = this.tokenService.getSubject(tokenJWT);
                var users = this.userRepository.getReferenceOneByUsername(subject);

                if (!users.isAccountNonLocked() || !users.isEnabled()) {
                    log.warn("User account is locked or disabled, {}", users.getUsername());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().println("User account is locked or disabled");
                    return;
                }

                var authentication = new UsernamePasswordAuthenticationToken(users, null,
                        users.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error("Authentication error: {}", exception.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(exception.getLocalizedMessage());
        }
    }

    private String getToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }
}
