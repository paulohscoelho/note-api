package notesapi.note.repository;

import notesapi.note.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findByUserUuid(UUID userId);
    Optional<NoteEntity> findByIdAndUserUuid(Long id, UUID userId);
}
