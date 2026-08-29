package notesapi.note.service;

import lombok.RequiredArgsConstructor;
import notesapi.note.dto.NoteRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.entity.NoteEntity;
import notesapi.common.exception.RegraNegocioException;
import notesapi.note.mapper.NoteMapper;
import notesapi.note.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoteService {
    private final NoteRepository repository;
    private final NoteMapper mapper;

    @Transactional
    public NoteResponse createNote(NoteRequest request){
        NoteEntity nota = mapper.toEntity(request);
        var savedEntity = repository.save(nota);
        return mapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> findAll(){
       return repository.findAll()
               .stream()
               .map(x->mapper.toResponse(x))
               .toList();
    }

    @Transactional(readOnly = true)
    public NoteResponse getId(Long id){
        NoteEntity note = repository.findById(id)
                .orElseThrow(()-> new RegraNegocioException("nota não encontrada."));
        return mapper.toResponse(note);
    }

    @Transactional
    public NoteResponse updateNote(Long id , NoteRequest request){
        var note = repository.findById(id)
                .orElseThrow(()-> new RegraNegocioException("nota não encontrada."));
        mapper.updateEntityFromDto(request,note);
        var updatedNote = repository.save(note);
        return mapper.toResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id){
        if (!repository.existsById(id)) {
            throw new RegraNegocioException("nota não encontrada.");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> findByTitle(String title){
        return repository.findByTitleContainingIgnoreCase(title)
                .stream().map(x -> mapper.toResponse(x)).toList();
    }
}
