package leonardo.labutilities.qualitylabpro.dtos.users;

public record RecoverPasswordDTO(String email, String temporaryPassword, String newPassword) {
}
