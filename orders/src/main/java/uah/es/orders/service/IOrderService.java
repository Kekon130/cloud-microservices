package uah.es.orders.service;

import uah.es.orders.dto.request.order.OrderNew;
import uah.es.orders.dto.response.order.OrderResponse;

import java.util.List;

public interface IOrderService {
    List<OrderResponse> findAll();
    OrderResponse findById(Integer id);
    Boolean exists(Integer id);
    OrderResponse save(OrderNew order);
    void deleteById(Integer id);
}
