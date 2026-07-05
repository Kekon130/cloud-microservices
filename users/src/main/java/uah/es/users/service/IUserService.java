package uah.es.users.service;

import uah.es.users.dto.request.UserNew;
import uah.es.users.dto.response.UserResponse;

import java.util.List;

public interface IUserService {
    List<UserResponse> findAll();
    UserResponse findById(Integer id);
    Boolean exists(Integer id);
    UserResponse save(UserNew userNew);
    void deleteById(Integer id);
}
