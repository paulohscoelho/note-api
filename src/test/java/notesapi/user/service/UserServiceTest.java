package notesapi.user.service;

import notesapi.user.dto.UserResponse;
import notesapi.user.entity.UserEntity;
import notesapi.user.mapper.UserMapper;
import notesapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import notesapi.common.exception.RegraNegocioException;
import notesapi.user.dto.UserRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
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

  @BeforeEach
  void setUp(){
    userUuid = UUID.randomUUID();

    userEntity = new UserEntity();
    userEntity.setUuid(userUuid);
    userEntity.setEmail("joao@test.com");
    userEntity.setPassword("$2a$10$hashfalso");

    userResponse = new UserResponse(userUuid, "joao@test.com");
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
  }
}