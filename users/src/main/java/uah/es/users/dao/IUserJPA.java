package uah.es.users.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uah.es.users.model.User;

public interface IUserJPA extends JpaRepository<User, Integer> {
}