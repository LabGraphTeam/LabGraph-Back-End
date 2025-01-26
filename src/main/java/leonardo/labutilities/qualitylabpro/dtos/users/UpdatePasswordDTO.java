package leonardo.labutilities.qualitylabpro.dtos.users;

public record UpdatePasswordDTO(String username, String email, String oldPassword, String newPassword) {
}
