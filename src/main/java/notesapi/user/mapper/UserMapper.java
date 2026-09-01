package notesapi.user.mapper;

import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "uuid",ignore = true)
    @Mapping(target = "notes",ignore = true)
    UserEntity toEntity(UserRequest request);

    UserResponse toResponse(UserEntity entity);
}
