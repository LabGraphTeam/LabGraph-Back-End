package leonardo.labutilities.qualitylabpro.domains.shared.authentication.utils;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import leonardo.labutilities.qualitylabpro.domains.users.models.User;

public final class AuthenticatedUserProvider {

    private AuthenticatedUserProvider() {}

    /**
     * Gets the current authenticated user.
     * 
     * @return The authenticated user or throws BadCredentialsException if not
     *         authenticated
     */
    public static User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User)) {
            throw new BadCredentialsException("User not authenticated");
        }

        return (User) authentication.getPrincipal();
    }
}
