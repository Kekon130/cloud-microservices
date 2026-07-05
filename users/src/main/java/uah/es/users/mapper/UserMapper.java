package uah.es.users.mapper;

import org.springframework.stereotype.Component;
import uah.es.users.dto.request.UserNew;
import uah.es.users.dto.response.UserResponse;
import uah.es.users.model.User;

@Component
public class UserMapper implements IUserMapper {
    @Override
    public User toNewUser(UserNew userNew) {
        User user = new User();
        user.setId(null);
        user.setName(userNew.getName());
        user.setSurname(userNew.getSurname());
        return user;
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        } else {
            return new UserResponse(
                    user.getId(),
                    user.getName().toUpperCase(),
                    user.getSurname().toUpperCase()
            );
        }
    }
}
