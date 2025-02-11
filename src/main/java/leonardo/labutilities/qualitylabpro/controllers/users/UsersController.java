package leonardo.labutilities.qualitylabpro.controllers.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.dtos.authentication.requests.LoginUserDTO;
import leonardo.labutilities.qualitylabpro.dtos.authentication.responses.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.requests.ForgotPasswordDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.requests.RecoverPasswordDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.requests.SignUpUsersDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.requests.UpdatePasswordDTO;
import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.services.users.UserService;

@SecurityRequirement(name = "bearer-key")
@RequestMapping("/users")
@RestController
public class UsersController {
    private final UserService userService;

    public UsersController(final UserService userService) {
        this.userService = userService;
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
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenJwtDTO> singIn(@RequestBody @Valid final LoginUserDTO loginUserDTO) {
        final var token =
                this.userService.signIn(loginUserDTO.identifier(), loginUserDTO.password());
        return ResponseEntity.ok(token);
    }
}
