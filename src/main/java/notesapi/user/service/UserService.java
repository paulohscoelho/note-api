package notesapi.user.service;

import lombok.RequiredArgsConstructor;
import notesapi.admin.dto.AdminUserResponse;
import notesapi.common.exception.RecursoNaoEncontradoException;
import notesapi.common.exception.RegraNegocioException;
import notesapi.user.dto.MeResponse;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.dto.UserWithNotesResponse;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import notesapi.user.mapper.UserMapper;
import notesapi.user.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminUserResponse updateRole(UUID targetUuid, Role newRole, UserEntity currentUser){
        if (targetUuid.equals(currentUser.getUuid())){
            throw new RegraNegocioException("Não é possivel alterar a própria role.");
        }
        UserEntity target = repository.findById(targetUuid)
            .orElseThrow(()-> new RecursoNaoEncontradoException("Usuário não encontrado."));
        target.setRole(newRole);
        UserEntity saved = repository.save(target);
        return AdminUserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(UUID uuid){
        UserEntity user = repository.findById(uuid)
            .orElseThrow(()->new RecursoNaoEncontradoException("Usuário não encontrado."));
        return mapper.toMeResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return repository
                .findByEmailContainingIgnoreCase(search, pageable)
                .map(mapper::toResponse);
        }
        return repository
            .findAll(pageable)
            .map(mapper::toResponse);
    }

    @Transactional
    public UserResponse createUser(UserRequest request){
        if (repository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Email já em uso " + request.email() );
        }
        UserEntity user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        var savedUser = repository.save(user);
        return mapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserWithNotesResponse getUserWithNotes(UUID uuid){
        UserEntity user = repository.findById(uuid)
                .orElseThrow(()->new RecursoNaoEncontradoException("Usuário não encontrado."));
        return mapper.toWithNotesResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID uuid){
        UserEntity user = repository.findById(uuid)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        return mapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email){
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(()->new RecursoNaoEncontradoException("Usuário não encontrado."));
        return mapper.toResponse(user);
    }

    @Transactional
    public void removeUser(UUID uuid){
        UserEntity user = repository.findById(uuid)
                .orElseThrow(()->new RecursoNaoEncontradoException("Usuário não encontrado."));
        repository.delete(user);
    }

}
