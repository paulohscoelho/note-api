package user.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID uuid,
        String email
) {
}
