package notesapi.user.dto;

import notesapi.note.dto.NoteSummaryResponse;

import java.util.List;
import java.util.UUID;

public record UserWithNotesResponse(
        UUID uuid,
        String email,
        List<NoteSummaryResponse> notes) {
}
