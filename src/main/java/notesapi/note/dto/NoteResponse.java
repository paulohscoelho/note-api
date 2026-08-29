package notesapi.note.dto;

import notesapi.user.dto.UserResponse;

import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        UserResponse user
) {
}
