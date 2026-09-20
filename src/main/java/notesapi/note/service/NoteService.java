package notesapi.note.service;

import lombok.RequiredArgsConstructor;
import notesapi.common.exception.RegraNegocioException;
import notesapi.note.dto.NoteRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.entity.NoteEntity;
import notesapi.note.mapper.NoteMapper;
import notesapi.note.repository.NoteRepository;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper mapper;

    @Transactional
    public NoteResponse createNote(NoteRequest request, UserEntity currentUser){
        NoteEntity note = mapper.toEntity(request);
        note.setUser(currentUser);
        var savedEntity = noteRepository.save(note);
        return mapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> findAllByUserUuid(UUID userId) {
        List<NoteEntity> notes = noteRepository.findByUserUuid(userId);
        return notes.stream()
                    .map(x -> mapper.toResponse(x))
                    .toList();
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> findAll(){
        return noteRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public NoteResponse findById(Long id, UserEntity currentUser) {
        NoteEntity note = loadAccessibleNote(id, currentUser);
        return mapper.toResponse(note);
    }


    @Transactional
    public NoteResponse updateNote(Long id , NoteRequest request,UserEntity currentUser){
        NoteEntity note = loadAccessibleNote(id,currentUser);
        mapper.updateEntityFromDto(request,note);
        NoteEntity updatedNote = noteRepository.save(note);
        return mapper.toResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id,UserEntity currentUser){
        NoteEntity note = loadAccessibleNote(id,currentUser);
        noteRepository.delete(note);
    }

    private NoteEntity loadAccessibleNote(Long id, UserEntity currentUser){
        if (currentUser.getRole() == Role.ADMIN){
            return noteRepository.findById(id)
                .orElseThrow(()->new RegraNegocioException("notas não encontrada"));
        }
        return noteRepository.findByIdAndUserUuid(id,currentUser.getUuid())
            .orElseThrow(()-> new RegraNegocioException("notas não encontrada"));
    }

}
