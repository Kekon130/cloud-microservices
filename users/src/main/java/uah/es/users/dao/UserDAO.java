package uah.es.users.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uah.es.users.model.User;

import java.util.List;

@Repository
public class UserDAO implements IUserDAO {
    private IUserJPA userJPA;

    @Autowired
    public UserDAO(IUserJPA userJPA) {
        this.userJPA = userJPA;
    }

    @Override
    public List<User> findAll() {
        return this.userJPA.findAll();
    }

    @Override
    public User findById(Integer id) {
        return this.userJPA.findById(id).orElse(null);
    }

    @Override
    public Boolean exists(Integer id) {
        return this.userJPA.existsById(id);
    }

    @Override
    public User save(User user) {
        return this.userJPA.save(user);
    }

    @Override
    public void deleteById(Integer id) {
        this.userJPA.deleteById(id);
    }
}
