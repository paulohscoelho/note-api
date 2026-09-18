package notesapi.user.mapper;

import notesapi.note.mapper.NoteMapper;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.dto.UserWithNotesResponse;
import notesapi.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = {NoteMapper.class})
public interface UserMapper {

    @Mapping(target = "uuid",ignore = true)
    @Mapping(target = "notes",ignore = true)
    @Mapping(target = "role",ignore = true)
    UserEntity toEntity(UserRequest request);

    UserResponse toResponse(UserEntity entity);

    UserWithNotesResponse toWithNotesResponse(UserEntity entity);
}
