package notesapi.admin.dto;

import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;

import java.util.UUID;

public record AdminUserResponse(
    UUID id,
    String email,
    Role role
) {
  public static AdminUserResponse from(UserEntity user){
    return new AdminUserResponse(user.getUuid(),user.getEmail(), user.getRole());
  }
}
