package uah.es.orders.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uah.es.orders.client.IProductsClient;
import uah.es.orders.client.IUsersClient;
import uah.es.orders.dto.request.order.OrderNew;
import uah.es.orders.dto.request.product.ProductRequest;
import uah.es.orders.dto.request.user.UserRequest;
import uah.es.orders.dto.response.order.OrderResponse;
import uah.es.orders.dto.response.product.ProductResponse;
import uah.es.orders.dto.response.user.UserResponse;
import uah.es.orders.model.Order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class OrderMapper implements IOrderMapper {

    @Value("${app.version:v1}")
    private String appVersion;

    @Override
    public Order toNewOrder(OrderNew orderNew) {
        Order order = new Order();
        order.setId(null);
        order.setUserId(orderNew.getUserId());
        order.setProductId(orderNew.getProductId());
        order.setUnits(orderNew.getQuantity());
        order.setDate(LocalDate.now());
        return order;
    }

    @Override
    public OrderResponse toOrderResponse(Order order, UserRequest user, ProductRequest product) {
        if (order == null) {
            return null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("uuuu/MM/dd");
            OrderResponse response = new OrderResponse(
                    order.getId(),
                    new UserResponse(user.getId(), user.getName().concat(" ").concat(user.getSurname())),
                    new ProductResponse(product.getId(), product.getName()),
                    order.getUnits(),
                    order.getUnits() * product.getPrice(),
                    order.getDate().format(formatter)
            );
            response.setVersion(appVersion);
            return response;
        }
    }
}
