package uah.es.users.mapper;

import uah.es.users.dto.request.UserNew;
import uah.es.users.dto.response.UserResponse;
import uah.es.users.model.User;

public interface IUserMapper {
    public User toNewUser(UserNew userNew);
    public UserResponse toUserResponse(User user);
}
