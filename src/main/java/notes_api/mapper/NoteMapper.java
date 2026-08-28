package notes_api.mapper;


import notes_api.dto.NoteRequest;
import notes_api.dto.NoteResponse;
import notes_api.entity.NoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NoteEntity toEntity (NoteRequest request);

    NoteResponse toResponse(NoteEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(NoteRequest request, @MappingTarget NoteEntity entity);


}
