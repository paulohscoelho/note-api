package notesapi.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.mapper.UserMapper;
import notesapi.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserResponse createUser(UserRequest request){
        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("Email já em uso " + request.email() );
        }

        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }


}
