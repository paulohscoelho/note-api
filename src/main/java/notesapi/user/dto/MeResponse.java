package notesapi.user.dto;

import notesapi.user.entity.Role;

import java.util.UUID;

public record MeResponse(
   UUID uuid,
   String email,
   Role role
) {
}
