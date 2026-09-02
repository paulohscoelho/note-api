package notesapi.note.mapper;


import notesapi.note.dto.NoteRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.dto.NoteSummaryResponse;
import notesapi.note.entity.NoteEntity;
import notesapi.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NoteEntity toEntity (NoteRequest request);

    NoteSummaryResponse toSummaryResponse(NoteEntity entity);

    NoteResponse toResponse(NoteEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(NoteRequest request, @MappingTarget NoteEntity entity);
}
