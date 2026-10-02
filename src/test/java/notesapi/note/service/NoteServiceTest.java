package notesapi.note.service;

import notesapi.common.exception.RecursoNaoEncontradoException;
import notesapi.note.dto.NoteRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.entity.NoteEntity;
import notesapi.note.mapper.NoteMapper;
import notesapi.note.repository.NoteRepository;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NoteService")
class NoteServiceTest {

  @Mock
  private NoteRepository noteRepository;

  @Mock
  private NoteMapper noteMapper;

  @InjectMocks
  private NoteService noteService;

  private UserEntity userComum;
  private UserEntity admin;
  private UUID userId;
  private UUID adminId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    adminId = UUID.randomUUID();

    userComum = new UserEntity();
    userComum.setUuid(userId);
    userComum.setRole(Role.USER);

    admin = new UserEntity();
    admin.setUuid(adminId);
    admin.setRole(Role.ADMIN);
  }

  @Nested
  @DisplayName("getNotes()")
  class GetNotes {

    @Test
    @DisplayName("deve retornar página de notas com filtro quando termo de busca é fornecido")
    void deveRetornarPaginaComFiltro_quandoSearchFornecido() {
      Pageable pageable = PageRequest.of(0, 10);
      String search = "java";

      NoteEntity nota = new NoteEntity();
      nota.setId(1L);
      NoteResponse response = new NoteResponse(1L, "java", "conteudo", null, null);

      Page<NoteEntity> paginas = new PageImpl<>(List.of(nota), pageable, 1);

      when(noteRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(search, search, pageable))
          .thenReturn(paginas);
      when(noteMapper.toResponse(nota)).thenReturn(response);

      Page<NoteResponse> resultado = noteService.getNotes(search, pageable);

      assertThat(resultado.getContent()).hasSize(1);
      assertThat(resultado.getContent().get(0)).isEqualTo(response);
      verify(noteRepository).findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(search, search, pageable);
      verify(noteRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("deve retornar todas as notas paginadas quando termo de busca é nulo ou em branco")
    void deveRetornarTodasPaginadas_quandoSearchNuloOuEmBranco() {
      Pageable pageable = PageRequest.of(0, 10);

      NoteEntity nota = new NoteEntity();
      nota.setId(1L);
      NoteResponse response = new NoteResponse(1L, "titulo", "conteudo", null, null);

      Page<NoteEntity> paginas = new PageImpl<>(List.of(nota), pageable, 1);

      when(noteRepository.findAll(pageable)).thenReturn(paginas);
      when(noteMapper.toResponse(nota)).thenReturn(response);

      Page<NoteResponse> resultado = noteService.getNotes("   ", pageable);

      assertThat(resultado.getContent()).hasSize(1);
      verify(noteRepository).findAll(pageable);
      verify(noteRepository, never()).findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(anyString(), anyString(), any(Pageable.class));
    }
  }

  @Nested
  @DisplayName("getUserNotes()")
  class GetUserNotes {

    @Test
    @DisplayName("deve retornar notas do usuário com filtro quando termo de busca é fornecido")
    void deveRetornarNotasDoUserComFiltro_quandoSearchFornecido() {
      Pageable pageable = PageRequest.of(0, 10);
      String search = "spring";

      NoteEntity nota = new NoteEntity();
      nota.setId(1L);
      nota.setUser(userComum);
      NoteResponse response = new NoteResponse(1L, "spring", "conteudo", null, null);

      Page<NoteEntity> paginas = new PageImpl<>(List.of(nota), pageable, 1);

      when(noteRepository.findByUserUuidAndTitleContainingIgnoreCaseOrUserUuidAndContentContainingIgnoreCase(
          userId, search, userId, search, pageable
      )).thenReturn(paginas);
      when(noteMapper.toResponse(nota)).thenReturn(response);

      Page<NoteResponse> resultado = noteService.getUserNotes(userId, search, pageable);

      assertThat(resultado.getContent()).hasSize(1);
      assertThat(resultado.getContent().get(0)).isEqualTo(response);
      verify(noteRepository).findByUserUuidAndTitleContainingIgnoreCaseOrUserUuidAndContentContainingIgnoreCase(
          userId, search, userId, search, pageable
      );
      verify(noteRepository, never()).findByUserUuid(any(UUID.class), any(Pageable.class));
    }

    @Test
    @DisplayName("deve retornar todas as notas do usuário paginadas quando termo de busca é nulo ou em branco")
    void deveRetornarTodasNotasDoUser_quandoSearchNuloOuEmBranco() {
      Pageable pageable = PageRequest.of(0, 10);

      NoteEntity nota = new NoteEntity();
      nota.setId(1L);
      nota.setUser(userComum);
      NoteResponse response = new NoteResponse(1L, "titulo", "conteudo", null, null);

      Page<NoteEntity> paginas = new PageImpl<>(List.of(nota), pageable, 1);

      when(noteRepository.findByUserUuid(userId, pageable)).thenReturn(paginas);
      when(noteMapper.toResponse(nota)).thenReturn(response);

      Page<NoteResponse> resultado = noteService.getUserNotes(userId, null, pageable);

      assertThat(resultado.getContent()).hasSize(1);
      verify(noteRepository).findByUserUuid(userId, pageable);
      verify(noteRepository, never()).findByUserUuidAndTitleContainingIgnoreCaseOrUserUuidAndContentContainingIgnoreCase(
          any(UUID.class), anyString(), any(UUID.class), anyString(), any(Pageable.class)
      );
    }
  }



  @Nested
  @DisplayName("createNote()")
  class CreateNote {

    @Test
    @DisplayName("deve atribuir o usuário autenticado como dono da nota")
    void deveAtribuirUserAutenticadoComoDono() {
      // Arrange
      NoteRequest request = new NoteRequest("meu título", "meu conteúdo");

      NoteEntity notaMapeada = new NoteEntity();
      notaMapeada.setTitle("meu título");
      notaMapeada.setContent("meu conteúdo");

      NoteResponse response = new NoteResponse(
          1L, "meu título", "meu conteúdo", null, null);

      when(noteMapper.toEntity(any(NoteRequest.class))).thenReturn(notaMapeada);
      when(noteRepository.save(any(NoteEntity.class))).thenReturn(notaMapeada);
      when(noteMapper.toResponse(notaMapeada)).thenReturn(response);

      // Act
      noteService.createNote(request, userComum);

      // Assert
      ArgumentCaptor<NoteEntity> captor = ArgumentCaptor.forClass(NoteEntity.class);
      verify(noteRepository).save(captor.capture());

      NoteEntity notaSalva = captor.getValue();
      assertThat(notaSalva.getUser()).isEqualTo(userComum);
    }

    @Test
    @DisplayName("deve retornar a response mapeada")
    void deveRetornarresponseMapeada() {
      // Arrange
      NoteRequest request = new NoteRequest("meu título", "meu conteúdo");

      NoteEntity notaMapeada = new NoteEntity();
      notaMapeada.setTitle("meu título");
      notaMapeada.setContent("meu conteúdo");

      NoteResponse response = new NoteResponse(
          1L, "meu título", "meu conteúdo", null, null);

      when(noteMapper.toEntity(any(NoteRequest.class))).thenReturn(notaMapeada);
      when(noteRepository.save(any(NoteEntity.class))).thenReturn(notaMapeada);
      when(noteMapper.toResponse(notaMapeada)).thenReturn(response);

      // Act
      NoteResponse resultado = noteService.createNote(request, userComum);

      // Assert
      assertThat(resultado).isEqualTo(response);
    }
  }


  @Nested
  @DisplayName("findById()")
  class FindById {

    @Test
    @DisplayName("deve retornar a nota quando o USER busca a própria nota")
    void deveRetornarNota_quandoUserBuscaNotaPropria() {
      NoteEntity notaDoUsuario = new NoteEntity();
      notaDoUsuario.setId(1L);
      notaDoUsuario.setUser(userComum);
      NoteResponse response = new NoteResponse(1L, "title", "content", null, null);

      when(noteRepository.findByIdAndUserUuid(1L, userId)).thenReturn(Optional.of(notaDoUsuario));
      when(noteMapper.toResponse(notaDoUsuario)).thenReturn(response);

      NoteResponse resultado = noteService.findById(1L, userComum);

      assertThat(resultado).isEqualTo(response);
      verify(noteRepository).findByIdAndUserUuid(1L, userId);
      verify(noteRepository, never()).findById(any());
    }

    @Test
    @DisplayName("deve retornar a nota quando o ADMIN busca nota de outro user")
    void deveRetornarNota_quandoAdminBuscaNotaDeOutroUser() {
      NoteEntity notaDeOutroUser = new NoteEntity();
      notaDeOutroUser.setId(1L);
      notaDeOutroUser.setUser(userComum);
      NoteResponse response = new NoteResponse(1L, "title", "content", null, null);

      when(noteRepository.findById(1L)).thenReturn(Optional.of(notaDeOutroUser));
      when(noteMapper.toResponse(notaDeOutroUser)).thenReturn(response);

      NoteResponse resultado = noteService.findById(1L, admin);

      assertThat(resultado).isEqualTo(response);
      verify(noteRepository).findById(1L);
      verify(noteRepository, never()).findByIdAndUserUuid(any(), any());
    }

    @Test
    @DisplayName("deve lançar RecursoNaoEncontradoException quando USER busca nota de outro user")
    void deveLancarRecursoNaoEncontrado_quandoUserBuscaNotaDeOutroUser() {
      when(noteRepository.findByIdAndUserUuid(1L, userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> noteService.findById(1L, userComum))
          .isInstanceOf(RecursoNaoEncontradoException.class)
          .hasMessage("nota não encontrada");

      verify(noteRepository).findByIdAndUserUuid(1L, userId);
      verify(noteRepository, never()).findById(any());
    }

    @Test
    @DisplayName("deve lançar RecursoNaoEncontradoException quando a nota não existe (admin)")
    void deveLancarRecursoNaoEncontrado_quandoNotaNaoExiste_paraAdmin() {
      when(noteRepository.findById(1L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> noteService.findById(1L, admin))
          .isInstanceOf(RecursoNaoEncontradoException.class)
          .hasMessage("nota não encontrada");

      verify(noteRepository).findById(1L);
      verify(noteRepository, never()).findByIdAndUserUuid(any(), any());
    }
  }

  @Nested
  @DisplayName("findAllByUserUuid()")
  class FindAllByUserUuid {

    @Test
    @DisplayName("deve retornar apenas as notas do usuário informado")
    void deveRetornarApenasNotasDoUser() {
      NoteEntity nota1 = new NoteEntity();
      nota1.setId(1L);
      nota1.setUser(userComum);

      NoteEntity nota2 = new NoteEntity();
      nota2.setId(2L);
      nota2.setUser(userComum);

      NoteResponse response1 = new NoteResponse(1L, "n1", "c1", null, null);
      NoteResponse response2 = new NoteResponse(2L, "n2", "c2", null, null);

      when(noteRepository.findByUserUuid(userId)).thenReturn(List.of(nota1, nota2));
      when(noteMapper.toResponse(nota1)).thenReturn(response1);
      when(noteMapper.toResponse(nota2)).thenReturn(response2);

      List<NoteResponse> resultado = noteService.findAllByUserUuid(userId);

      assertThat(resultado).hasSize(2);
      assertThat(resultado).containsExactly(response1, response2);
      verify(noteRepository).findByUserUuid(userId);
    }
  }

  @Nested
  @DisplayName("findAll()")
  class FindAll {

    @Test
    @DisplayName("deve retornar todas as notas, indiferente do dono")
    void deveRetornarTodasNotas() {
      NoteEntity nota1 = new NoteEntity();
      nota1.setId(1L);
      nota1.setUser(userComum);

      NoteEntity nota2 = new NoteEntity();
      nota2.setId(2L);
      nota2.setUser(admin);

      NoteEntity nota3 = new NoteEntity();
      nota3.setId(3L);
      nota3.setUser(userComum);

      NoteResponse r1 = new NoteResponse(1L, "n1", "c1", null, null);
      NoteResponse r2 = new NoteResponse(2L, "n2", "c2", null, null);
      NoteResponse r3 = new NoteResponse(3L, "n3", "c3", null, null);

      when(noteRepository.findAll()).thenReturn(List.of(nota1, nota2, nota3));
      when(noteMapper.toResponse(nota1)).thenReturn(r1);
      when(noteMapper.toResponse(nota2)).thenReturn(r2);
      when(noteMapper.toResponse(nota3)).thenReturn(r3);

      List<NoteResponse> resultado = noteService.findAll();

      assertThat(resultado).hasSize(3);
      assertThat(resultado).containsExactly(r1, r2, r3);
      verify(noteRepository).findAll();
    }
  }

  @Nested
  @DisplayName("updateNote()")
  class UpdateNote {

    @Test
    @DisplayName("deve atualizar e salvar a nota do próprio user")
    void deveAtualizarNotaDoProprioUser() {
      // Arrange
      NoteEntity notaExistente = new NoteEntity();
      notaExistente.setId(1L);
      notaExistente.setTitle("título antigo");
      notaExistente.setContent("conteúdo antigo");
      notaExistente.setUser(userComum);

      NoteRequest request = new NoteRequest("título novo", "conteúdo novo");

      NoteResponse response = new NoteResponse(
          1L, "título novo", "conteúdo novo", null, null);

      when(noteRepository.findByIdAndUserUuid(1L, userId))
          .thenReturn(Optional.of(notaExistente));
      when(noteRepository.save(any(NoteEntity.class))).thenReturn(notaExistente);
      when(noteMapper.toResponse(notaExistente)).thenReturn(response);

      // Act
      noteService.updateNote(1L, request, userComum);

      // Assert — o mapper foi chamado com o request e a nota corretos
      verify(noteMapper).updateEntityFromDto(request, notaExistente);
      // Assert — a nota foi salva
      verify(noteRepository).save(notaExistente);
    }

    @Test
    @DisplayName("deve retornar a response mapeada da nota atualizada")
    void deveRetornarresponseMapeada() {

      NoteEntity notaExistente = new NoteEntity();
      notaExistente.setId(1L);
      notaExistente.setUser(userComum);

      NoteRequest request = new NoteRequest("título novo", "conteúdo novo");

      NoteResponse response = new NoteResponse(
          1L, "título novo", "conteúdo novo", null, null);

      when(noteRepository.findByIdAndUserUuid(1L, userId))
          .thenReturn(Optional.of(notaExistente));
      when(noteRepository.save(any(NoteEntity.class))).thenReturn(notaExistente);
      when(noteMapper.toResponse(notaExistente)).thenReturn(response);

      NoteResponse resultado = noteService.updateNote(1L, request, userComum);

      assertThat(resultado).isEqualTo(response);
    }

    @Test
    @DisplayName("NÃO deve salvar quando user tenta atualizar nota de outro user")
    void naoDeveSalvarQuandoUserTentaAtualizarNotaAlheia() {
      when(noteRepository.findByIdAndUserUuid(1L, userId)).thenReturn(Optional.empty());
      NoteRequest request = new NoteRequest("título novo", "conteúdo novo");

      assertThatThrownBy(() -> noteService.updateNote(1L, request, userComum))
          .isInstanceOf(RecursoNaoEncontradoException.class)
          .hasMessage("nota não encontrada");

      verify(noteRepository, never()).save(any());
      verify(noteMapper, never()).updateEntityFromDto(any(), any());
    }
  }

  @Nested
  @DisplayName("deleteNote()")
  class DeleteNote {

    @Test
    @DisplayName("deve deletar a nota do próprio user")
    void deveDeletarNotaDoProprioUser() {

      NoteEntity notaDoUser = new NoteEntity();
      notaDoUser.setId(1L);
      notaDoUser.setUser(userComum);

      when(noteRepository.findByIdAndUserUuid(1L, userId))
          .thenReturn(Optional.of(notaDoUser));

      noteService.deleteNote(1L, userComum);

      verify(noteRepository).delete(notaDoUser);
    }

    @Test
    @DisplayName("NÃO deve deletar quando user tenta deletar nota de outro user")
    void naoDeveDeletarQuandoUserTentaDeletarNotaAlheia() {

      when(noteRepository.findByIdAndUserUuid(1L, userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> noteService.deleteNote(1L, userComum))
          .isInstanceOf(RecursoNaoEncontradoException.class)
          .hasMessage("nota não encontrada");

      verify(noteRepository, never()).delete(any());
      verify(noteRepository, never()).deleteById(any());
    }
  }
}