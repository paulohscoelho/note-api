package notesapi.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "uuid",ignore = true)
    @Mapping(target = "notes",ignore = true)
    UserEntity toEntity(UserRequest request);

    UserResponse toResponse(UserEntity entity);


}
