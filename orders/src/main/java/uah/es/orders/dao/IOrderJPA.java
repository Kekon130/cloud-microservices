package uah.es.orders.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uah.es.orders.model.Order;

public interface IOrderJPA extends JpaRepository<Order, Integer> {
}