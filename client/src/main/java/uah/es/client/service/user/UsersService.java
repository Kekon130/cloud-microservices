package uah.es.client.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uah.es.client.client.IUserClient;
import uah.es.client.dto.request.user.UserRequest;
import uah.es.client.dto.response.user.UserResponse;
import uah.es.client.paginator.PageUtils;
import uah.es.client.service.HeadersUtils;

@Service
public class UsersService implements IUserService {
    private final IUserClient usersClient;

    @Autowired
    public UsersService(IUserClient usersClient) {
        this.usersClient = usersClient;
    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        return PageUtils.toPage(this.usersClient.findAll(HeadersUtils.generateTrafficType()), pageable);
    }

    @Override
    public UserResponse findById(Integer id) {
        UserResponse user = null;
        if (id != null && id > 0) {
            user = this.usersClient.findById(HeadersUtils.generateTrafficType(), id);
        }
        return user;
    }

    @Override
    public UserResponse save(UserRequest user) {
        UserResponse response = null;
        if (user != null) {
            response = this.usersClient.save(HeadersUtils.generateTrafficType(), user);
        }
        return response;
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null && id > 0) {
            this.usersClient.deleteById(HeadersUtils.generateTrafficType(), id);
        }
    }
}
