package uah.es.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uah.es.users.dao.IUserDAO;
import uah.es.users.dto.request.UserNew;
import uah.es.users.dto.response.UserResponse;
import uah.es.users.mapper.IUserMapper;

import java.util.List;

@Service
public class UserService implements IUserService {
    private IUserDAO userDAO;
    private IUserMapper userMapper;

    @Autowired
    public UserService(IUserDAO userDAO, IUserMapper userMapper) {
        this.userDAO = userDAO;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponse> findAll() {
        return this.userDAO.findAll().stream().map(this.userMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse findById(Integer id) {
        return this.userMapper.toUserResponse(this.userDAO.findById(id));
    }

    @Override
    public Boolean exists(Integer id) {
        Boolean result = false;
        if (id != null && id > 0) {
            result = this.userDAO.exists(id);
        }
        return result;
    }

    @Override
    public UserResponse save(UserNew userNew) {
        UserResponse savedUser = null;
        if (userNew != null) {
            savedUser = this.userMapper.toUserResponse(this.userDAO.save(this.userMapper.toNewUser(userNew)));
        }
        return savedUser;
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null && id > 0) {
            this.userDAO.deleteById(id);
        }
    }
}
