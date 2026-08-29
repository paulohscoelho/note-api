package notes_api.dto;

import user.dto.UserResponse;

import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        UserResponse user
) {
}
