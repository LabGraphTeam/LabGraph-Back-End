package leonardo.labutilities.qualitylabpro.controllers.users;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.dtos.authentication.LoginUserRecord;
import leonardo.labutilities.qualitylabpro.dtos.authentication.TokenJwtRecord;
import leonardo.labutilities.qualitylabpro.dtos.users.RecoverPasswordRecord;
import leonardo.labutilities.qualitylabpro.dtos.users.SignUpUsersRecord;
import leonardo.labutilities.qualitylabpro.dtos.users.UpdatePasswordRecord;
import leonardo.labutilities.qualitylabpro.dtos.users.UsersRecord;
import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.services.users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody final UpdatePasswordRecord updatePasswordRecord) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            final var user = (User) authentication.getPrincipal();
            userService.updateUserPassword(user.getUsername(), user.getEmail(),
                                           updatePasswordRecord.oldPassword(), updatePasswordRecord.newPassword());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/password/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody final UsersRecord usersRecord) {
        userService.recoverPassword(usersRecord.username(), usersRecord.email());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("/password/recover")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody final RecoverPasswordRecord recoverPasswordRecord) {
        userService.changePassword(recoverPasswordRecord.email(),
                                   recoverPasswordRecord.temporaryPassword(), recoverPasswordRecord.newPassword());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody final SignUpUsersRecord signUpUsersRecord) {
        userService.signUp(signUpUsersRecord.username(), signUpUsersRecord.email(), signUpUsersRecord.password());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenJwtRecord> singIn(
            @RequestBody @Valid final LoginUserRecord loginUserRecord) {
        final var token = userService.signIn(loginUserRecord.email(),
                                             loginUserRecord.password());
        return ResponseEntity.ok(token);
    }
}
