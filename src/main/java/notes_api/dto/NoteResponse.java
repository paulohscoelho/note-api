package notes_api.dto;

import notes_api.entity.NoteEntity;

import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
