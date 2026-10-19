package notesapi.note.dto;

import java.time.LocalDateTime;

public record NoteSummaryResponse(
    Long id,
    String title,
    String content,
    Integer priority,
    LocalDateTime dueDate,
    LocalDateTime createdAt
        ) {
}
