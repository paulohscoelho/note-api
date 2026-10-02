package notesapi.note.repository;

import notesapi.note.entity.NoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    List<NoteEntity> findByUserUuid(UUID userId);

    Optional<NoteEntity> findByIdAndUserUuid(Long id, UUID userId);

    Page<NoteEntity> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
        String title, String content, Pageable pageable
    );

    Page<NoteEntity> findByUserUuid(UUID userId, Pageable pageable);

    Page<NoteEntity> findByUserUuidAndTitleContainingIgnoreCaseOrUserUuidAndContentContainingIgnoreCase(
        UUID userIdTitle, String title, UUID userIdContent, String content, Pageable pageable
    );

}