package uah.es.orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uah.es.orders.client.IProductsClient;
import uah.es.orders.client.IUsersClient;
import uah.es.orders.dao.IOrderDAO;
import uah.es.orders.dto.request.order.OrderNew;
import uah.es.orders.dto.request.product.ProductRequest;
import uah.es.orders.dto.request.user.UserRequest;
import uah.es.orders.dto.response.order.OrderResponse;
import uah.es.orders.mapper.IOrderMapper;
import uah.es.orders.model.Order;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService implements IOrderService {
    private IOrderDAO orderDAO;
    private IOrderMapper orderMapper;
    private IUsersClient usersClient;
    private IProductsClient productsClient;

    @Autowired
    public OrderService(IOrderDAO orderDAO, IOrderMapper orderMapper, IUsersClient usersClient, IProductsClient productsClient) {
        this.orderDAO = orderDAO;
        this.orderMapper = orderMapper;
        this.usersClient = usersClient;
        this.productsClient = productsClient;
    }

    @Override
    public List<OrderResponse> findAll() {
        return this.orderDAO.findAll()
                .stream()
                .map(order -> {
                    UserRequest user = this.usersClient.getUserById(this.generateTrafficType(), order.getUserId());
                    ProductRequest product = this.productsClient.getProductById(this.generateTrafficType(), order.getProductId());
                    return this.orderMapper.toOrderResponse(order, user, product);
                })
                .toList();
    }

    @Override
    public OrderResponse findById(Integer id) {
        OrderResponse response = null;
        if (id != null && id > 0) {
            Order order = this.orderDAO.findById(id);

            if (order != null) {
                UserRequest user = this.usersClient.getUserById(this.generateTrafficType(), order.getUserId());
                ProductRequest product = this.productsClient.getProductById(this.generateTrafficType(), order.getProductId());
                response = this.orderMapper.toOrderResponse(order, user, product);
            }
        }
        return response;
    }

    @Override
    public Boolean exists(Integer id) {
        return this.orderDAO.exists(id);
    }

    @Override
    public OrderResponse save(OrderNew order) {
        OrderResponse response = null;
        if (order != null) {
            UserRequest user = this.usersClient.getUserById(this.generateTrafficType(), order.getUserId());
            ProductRequest product = this.productsClient.getProductById(this.generateTrafficType(), order.getProductId());

            if (user != null && product != null) {
                response = this.orderMapper.toOrderResponse(
                            this.orderDAO.save(this.orderMapper.toNewOrder(order)),
                        user,
                        product
                );
            }
        }

        return response;
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null && id > 0) {
            this.orderDAO.deleteById(id);
        }
    }

    private String generateTrafficType() {
        return ThreadLocalRandom.current().nextBoolean()
                ? "v1"
                : "v2";
    }
}
