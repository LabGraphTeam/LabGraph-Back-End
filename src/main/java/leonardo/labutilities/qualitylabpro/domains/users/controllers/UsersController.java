package leonardo.labutilities.qualitylabpro.domains.users.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.dtos.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.ForgotPasswordDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.RecoverPasswordDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.SignInUserDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.SignUpUsersDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.UpdatePasswordDTO;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import leonardo.labutilities.qualitylabpro.domains.users.services.UserService;

@SecurityRequirement(name = "bearer-key")
@RequestMapping("/users")
@RestController
public class UsersController {
    private final UserService userService;

    public UsersController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/validated-analytics")
    public ResponseEntity<Page<AnalyticsDTO>> getAnalyticsValidatedByUserId(Pageable pageable) {
        return ResponseEntity.ok(this.userService.findAnalyticsByUserValidated(pageable));
    }

    @Transactional
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody final UpdatePasswordDTO updatePasswordDTO) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            final var user = (User) authentication.getPrincipal();
            this.userService.updateUserPassword(user.getUsername(), user.getEmail(),
                    updatePasswordDTO.oldPassword(), updatePasswordDTO.newPassword());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/password/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody final ForgotPasswordDTO forgotPasswordDTO) {
        this.userService.recoverPassword(forgotPasswordDTO.username(), forgotPasswordDTO.email());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("/password/recover")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody final RecoverPasswordDTO recoverPasswordDTO) {
        this.userService.changePassword(recoverPasswordDTO.email(),
                recoverPasswordDTO.temporaryPassword(), recoverPasswordDTO.newPassword());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody final SignUpUsersDTO signUpUsersDTO) {
        this.userService.signUp(signUpUsersDTO.identifier(), signUpUsersDTO.email(),
                signUpUsersDTO.password());
        return ResponseEntity.created(URI.create("/users")).build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenJwtDTO> singIn(
            @RequestBody @Valid final SignInUserDTO loginUserDTO) {
        final var token =
                this.userService.signIn(loginUserDTO.identifier(), loginUserDTO.password());
        return ResponseEntity.ok(token);
    }
}
