package uah.es.orders.dao;

import uah.es.orders.model.Order;

import java.util.List;

public interface IOrderDAO {
    List<Order> findAll();
    Order findById(Integer id);
    Boolean exists(Integer id);
    Order save(Order order);
    void deleteById(Integer id);
}
