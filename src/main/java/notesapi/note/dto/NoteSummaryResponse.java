package notesapi.note.dto;

import java.time.LocalDateTime;

public record NoteSummaryResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt
        ) {
}
