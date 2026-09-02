package notesapi.user.service;

import lombok.RequiredArgsConstructor;
import notesapi.common.exception.RegraNegocioException;
import notesapi.user.dto.UserWithNotesResponse;
import notesapi.user.entity.UserEntity;
import org.springframework.stereotype.Service;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.mapper.UserMapper;
import notesapi.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    public UserResponse createUser(UserRequest request){
        if (repository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Email já em uso " + request.email() );
        }

        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public UserWithNotesResponse getUserWithNotes(UUID uuid){
        UserEntity user = repository.findById(uuid)
                .orElseThrow(()->new RegraNegocioException("Usuario não encontrado"));
        return mapper.toWithNotesResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID uuid){
        UserEntity user = repository.findById(uuid)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));
        return mapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email){
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(()->new RegraNegocioException("Usuário não encontrado."));
        return mapper.toResponse(user);
    }

    @Transactional
    public void removeUser(UUID uuid){
        UserEntity user = repository.findById(uuid)
                .orElseThrow(()->new RegraNegocioException("Usuario não encontrado."));

        repository.delete(user);
    }

}
