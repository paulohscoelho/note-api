package user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import user.dto.UserRequest;
import user.dto.UserResponse;
import user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "uuid",ignore = true)
    @Mapping(target = "notes",ignore = true)
    UserEntity toEntity(UserRequest request);

    UserResponse toResponse(UserEntity entity);


}
