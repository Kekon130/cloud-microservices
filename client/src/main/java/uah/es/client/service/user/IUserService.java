package uah.es.client.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uah.es.client.dto.request.user.UserRequest;
import uah.es.client.dto.response.user.UserResponse;

public interface IUserService {
    Page<UserResponse> findAll(Pageable pageable);
    UserResponse findById(Integer id);
    UserResponse save(UserRequest user);
    void deleteById(Integer id);
}
