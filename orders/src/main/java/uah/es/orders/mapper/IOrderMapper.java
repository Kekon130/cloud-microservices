package uah.es.orders.mapper;

import uah.es.orders.dto.request.order.OrderNew;
import uah.es.orders.dto.request.product.ProductRequest;
import uah.es.orders.dto.request.user.UserRequest;
import uah.es.orders.dto.response.order.OrderResponse;
import uah.es.orders.model.Order;

public interface IOrderMapper {
    Order toNewOrder(OrderNew orderNew);
    OrderResponse toOrderResponse(Order order, UserRequest user, ProductRequest product);
}
