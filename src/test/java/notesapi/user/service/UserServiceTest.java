package notesapi.user.service;

import notesapi.common.exception.RecursoNaoEncontradoException;
import notesapi.common.exception.RegraNegocioException;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.dto.UserWithNotesResponse;
import notesapi.user.entity.UserEntity;
import notesapi.user.mapper.UserMapper;
import notesapi.user.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private UUID userUuid;
  private UserEntity userEntity;
  private UserResponse userResponse;
  private UserWithNotesResponse userWithNotesResponse;

  @BeforeEach
  void setUp(){
    userUuid = UUID.randomUUID();

    userEntity = new UserEntity();
    userEntity.setUuid(userUuid);
    userEntity.setEmail("joao@test.com");
    userEntity.setPassword("$2a$10$hashfalso");

    userResponse = new UserResponse(userUuid, "joao@test.com");
    userWithNotesResponse = new UserWithNotesResponse(userUuid, "joao@test.com", List.of());
  }

  @Nested
  @DisplayName("getUsers")
  class GetUsers {

    @Test
    @DisplayName("deve retornar pagina de usuarios com filtro quando email é fornecido")
    void deveRetornarPaginaComFiltro_quandoEmailFornecido() {
      Pageable pageable = PageRequest.of(0, 10);
      String search = "joao";

      Page<UserEntity> paginaEntidades = new PageImpl<>(List.of(userEntity), pageable, 1);

      when(userRepository.findByEmailContainingIgnoreCase(search, pageable))
          .thenReturn(paginaEntidades);
      when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

      Page<UserResponse> resultado = userService.getUsers(search, pageable);

      assertThat(resultado.getContent()).hasSize(1);
      assertThat(resultado.getContent().get(0)).isEqualTo(userResponse);
      verify(userRepository).findByEmailContainingIgnoreCase(search, pageable);
      verify(userRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("deve retornar todos os usuarios paginados quando email é nulo ou em branco")
    void deveRetornarTodosPaginados_quandoEmailNuloOuEmBranco() {
      Pageable pageable = PageRequest.of(0, 10);

      Page<UserEntity> paginaEntidades = new PageImpl<>(List.of(userEntity), pageable, 1);

      when(userRepository.findAll(pageable)).thenReturn(paginaEntidades);
      when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

      Page<UserResponse> resultado = userService.getUsers("   ", pageable);

      assertThat(resultado.getContent()).hasSize(1);
      assertThat(resultado.getContent().get(0)).isEqualTo(userResponse);
      verify(userRepository).findAll(pageable);
      verify(userRepository, never()).findByEmailContainingIgnoreCase(anyString(), any(Pageable.class));
    }
  }

  @Nested
  @DisplayName("createUser")
  class CreateUser{
    @Test
    @DisplayName("deve salvar user com senha encriptada")
    void deveSalvarUserComSenhaEncriptada(){
      UserRequest request = new UserRequest("joao@test.com","senha12345");

      when(userRepository.existsByEmail("joao@test.com")).thenReturn(false);
      when(userMapper.toEntity(request)).thenReturn(userEntity);
      when(passwordEncoder.encode("senha12345")).thenReturn("$2a$10$hashfalso");
      when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
      when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

      userService.createUser(request);

      ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
      verify(userRepository).save(captor.capture());

      UserEntity salvo = captor.getValue();
      assertThat(salvo.getPassword()).isEqualTo("$2a$10$hashfalso");
      assertThat(salvo.getPassword()).isNotEqualTo("senha12345");

      verify(passwordEncoder).encode("senha12345");
    }

    @Test
    @DisplayName("deve retornar a resposta mapeada")
    void deveRetornarRespostaMapeada(){
      UserRequest request= new UserRequest("joao@test.com","senha12345");

      when(userRepository.existsByEmail("joao@test.com")).thenReturn(false);
      when(userMapper.toEntity(request)).thenReturn(userEntity);
      when(passwordEncoder.encode("senha12345")).thenReturn("$2a$10$hashfalso");
      when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
      when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

      UserResponse resultado = userService.createUser(request);
      assertThat(resultado).isEqualTo(userResponse);
    }

    @Test
    @DisplayName("deve lancar RegraNegoioException quando o email ja existe")
    void deveLancarRegraNegocioException_quandoEmailExiste(){
      UserRequest request = new UserRequest("joao@test.com","senha12345");

      when(userRepository.existsByEmail("joao@test.com")).thenReturn(true);

      assertThatThrownBy(()-> userService.createUser(request))
          .isInstanceOf(RegraNegocioException.class)
          .hasMessage("Email já em uso joao@test.com");

      verify(userRepository, never()).save(any());
      verifyNoInteractions(userMapper);
      verifyNoInteractions(passwordEncoder);
    }
  }

  @Nested
  @DisplayName("getUserWithNotes")
  class GetUserWithNotes{

    @Test
    @DisplayName("deve retornar o usuario com suas notas")
    void deveRetornarUserComNotas(){
      when(userRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));
      when(userMapper.toWithNotesResponse(userEntity)).thenReturn(userWithNotesResponse);

      UserWithNotesResponse resultado = userService.getUserWithNotes(userUuid);

      assertThat(resultado).isEqualTo(userWithNotesResponse);
      verify(userRepository).findById(userUuid);
      verify(userMapper).toWithNotesResponse(userEntity);
    }

    @Test
    @DisplayName("deve lancar RecursoNaoEncontradoException quando o usuario não existir")
    void deveLancarRecursoNaoEncontrado_quandoUserNaoExiste(){
      when(userRepository.findById(userUuid)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.getUserWithNotes(userUuid))
          .isInstanceOf(RecursoNaoEncontradoException.class)
          .hasMessage("Usuário não encontrado.");

      verify(userRepository).findById(userUuid);
      verifyNoInteractions(userMapper);
    }
  }

  @Nested
  @DisplayName("getUserById")
  class GetUserById{
    @Test
    @DisplayName("deve retornar o UserResponse do usuario encontrado")
    void deveRetornarUserResponse(){
      when(userRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));
      when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

      UserResponse resultado = userService.getUserById(userUuid);

      assertThat(resultado).isEqualTo(userResponse);
      verify(userRepository).findById(userUuid);
      verify(userMapper).toResponse(userEntity);
    }

    @Test
    @DisplayName("deve lancar RecursoNaoEncontradoException quando o usuario não existir")
    void deveLancarRecursoNaoEncontrado_quandoUserNaoExiste(){
      when(userRepository.findById(userUuid)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.getUserById(userUuid))
          .isInstanceOf(RecursoNaoEncontradoException.class)
          .hasMessage("Usuário não encontrado.");

      verify(userRepository).findById(userUuid);
      verifyNoInteractions(userMapper);
    }
  }

  @Nested
  @DisplayName("getUserByEmail")
  class GetUserByEmail{
    @Test
    @DisplayName("deve retornar o UserResponse quando o email existir")
    void deveRetornarUserResponse(){
      String email = "joao@teste.com";
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
      when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

      UserResponse resultado = userService.getUserByEmail(email);
      assertThat(resultado).isEqualTo(userResponse);
      verify(userRepository).findByEmail(email);
      verify(userMapper).toResponse(userEntity);
    }

    @Test
    @DisplayName("deve lançar RecursoNaoEncontradoException quando o email não existe")
    void deveLancarRecursoNaoEncontrado_quandoEmailNaoExiste() {
      String email = "naoExiste@teste.com";
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      assertThatThrownBy(()->userService.getUserByEmail(email))
          .isInstanceOf(RecursoNaoEncontradoException.class)
          .hasMessage("Usuário não encontrado.");

      verify(userRepository).findByEmail(email);
      verifyNoInteractions(userMapper);
    }
  }

  @Nested
  @DisplayName("removeUser()")
  class RemoveUser {

    @Test
    @DisplayName("deve deletar o user encontrado")
    void deveDeletarUser() {
      when(userRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));

      userService.removeUser(userUuid);

      verify(userRepository).findById(userUuid);
      verify(userRepository).delete(userEntity);
    }

    @Test
    @DisplayName("deve lançar RecursoNaoEncontradoException e não deletar quando o user não existir")
    void deveLancarRecursoNaoEncontradoENaoDeletar_quandoUserNaoExiste() {
      when(userRepository.findById(userUuid)).thenReturn(Optional.empty());

      assertThatThrownBy(()->userService.removeUser(userUuid))
          .isInstanceOf(RecursoNaoEncontradoException.class)
          .hasMessage("Usuário não encontrado.");

      verify(userRepository).findById(userUuid);
      verify(userRepository,never()).delete(any());
      verifyNoInteractions(userMapper);
    }
  }
}