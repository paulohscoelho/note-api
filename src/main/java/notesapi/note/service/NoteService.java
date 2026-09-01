package notesapi.note.service;

import lombok.RequiredArgsConstructor;
import notesapi.note.dto.NoteRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.entity.NoteEntity;
import notesapi.common.exception.RegraNegocioException;
import notesapi.note.mapper.NoteMapper;
import notesapi.note.repository.NoteRepository;
import notesapi.user.entity.UserEntity;
import notesapi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteMapper mapper;

    @Transactional
    public NoteResponse createNote(NoteRequest request, UUID userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new RegraNegocioException("usuário não encontrado."));

        NoteEntity note = mapper.toEntity(request);
        note.setUser(user);
        var savedEntity = noteRepository.save(note);
        return mapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> findAllByUserUuid(UUID userId) {
        List<NoteEntity> notes = noteRepository.findByUserUuid(userId);

        return notes.stream().map(x -> mapper.toResponse(x))
                .toList();
    }


    @Transactional(readOnly = true)
    public NoteResponse findByIdAndUserUuid(Long id, UUID userId){
        NoteEntity note = noteRepository.findByIdAndUserUuid(id, userId)
                .orElseThrow(()-> new RegraNegocioException("nota não encontrada."));
        return mapper.toResponse(note);
    }

    @Transactional
    public NoteResponse updateNote(Long id , NoteRequest request,UUID userId){
        NoteEntity note = noteRepository.findByIdAndUserUuid(id, userId)
                .orElseThrow(()-> new RegraNegocioException("nota não encontrada."));

        mapper.updateEntityFromDto(request,note);
        NoteEntity updatedNote = noteRepository.save(note);
        return mapper.toResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id,UUID userId){
        NoteEntity note = noteRepository.findByIdAndUserUuid(id, userId)
                .orElseThrow(()-> new RegraNegocioException("nota não encontrada."));
        noteRepository.delete(note);
    }


}
