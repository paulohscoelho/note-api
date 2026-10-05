package notesapi.admin.dto;

import jakarta.validation.constraints.NotNull;
import notesapi.user.entity.Role;

public record RoleUpdateRequest(
    @NotNull(message = "A role é obrigatória.")
    Role role
) {
}
