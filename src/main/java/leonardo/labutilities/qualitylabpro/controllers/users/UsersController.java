package leonardo.labutilities.qualitylabpro.controllers.users;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.dtos.authentication.LoginUserDTO;
import leonardo.labutilities.qualitylabpro.dtos.authentication.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.RecoverPasswordDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.SignUpUsersDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.UpdatePasswordDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.UsersDTO;
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
            @RequestBody final UpdatePasswordDTO updatePasswordDTO) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            final var user = (User) authentication.getPrincipal();
            userService.updateUserPassword(user.getUsername(), user.getEmail(),
                                           updatePasswordDTO.oldPassword(), updatePasswordDTO.newPassword());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/password/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody final UsersDTO usersDTO) {
        userService.recoverPassword(usersDTO.username(), usersDTO.email());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("/password/recover")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody final RecoverPasswordDTO recoverPasswordDTO) {
        userService.changePassword(recoverPasswordDTO.email(),
                                   recoverPasswordDTO.temporaryPassword(), recoverPasswordDTO.newPassword());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody final SignUpUsersDTO signUpUsersDTO) {
        userService.signUp(signUpUsersDTO.username(), signUpUsersDTO.email(), signUpUsersDTO.password());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenJwtDTO> singIn(
            @RequestBody @Valid final LoginUserDTO loginUserDTO) {
        final var token = userService.signIn(loginUserDTO.email(),
                                             loginUserDTO.password());
        return ResponseEntity.ok(token);
    }
}
