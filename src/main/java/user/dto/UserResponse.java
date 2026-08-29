package user.dto;

import java.util.UUID;

public record UserResponse(
        UUID uuid,
        String email
) {
}
