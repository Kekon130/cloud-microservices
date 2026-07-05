package uah.es.orders.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uah.es.orders.model.Order;

import java.util.List;

@Repository
public class OrderDAO implements IOrderDAO {
    private IOrderJPA orderJPA;

    @Autowired
    public OrderDAO(IOrderJPA orderJPA) {
        this.orderJPA = orderJPA;
    }

    @Override
    public List<Order> findAll() {
        return this.orderJPA.findAll();
    }

    @Override
    public Order findById(Integer id) {
        return this.orderJPA.findById(id).orElse(null);
    }

    @Override
    public Boolean exists(Integer id) {
        return this.orderJPA.existsById(id);
    }

    @Override
    public Order save(Order order) {
        return this.orderJPA.save(order);
    }

    @Override
    public void deleteById(Integer id) {
        this.orderJPA.deleteById(id);
    }
}
