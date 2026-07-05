package uah.es.users.dao;

import uah.es.users.model.User;

import java.util.List;

public interface IUserDAO {
    List<User> findAll();
    User findById(Integer id);
    Boolean exists(Integer id);
    User save(User user);
    void deleteById(Integer id);
}
