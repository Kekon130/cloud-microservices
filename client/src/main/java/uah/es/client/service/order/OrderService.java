package uah.es.client.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uah.es.client.client.IOrderClient;
import uah.es.client.client.IProductClient;
import uah.es.client.client.IUserClient;
import uah.es.client.dto.request.order.OrderRequest;
import uah.es.client.dto.response.order.OrderResponse;
import uah.es.client.paginator.PageUtils;
import uah.es.client.service.HeadersUtils;

@Service
public class OrderService implements IOrderService {
    private final IOrderClient orderClient;
    private final IUserClient userClient;
    private final IProductClient productClient;

    @Autowired
    public OrderService(IOrderClient orderClient, IUserClient userClient, IProductClient productClient) {
        this.orderClient = orderClient;
        this.userClient = userClient;
        this.productClient = productClient;
    }

    @Override
    public Page<OrderResponse> findAll(Pageable pageable) {
        return PageUtils.toPage(this.orderClient.findAll(HeadersUtils.generateTrafficType()), pageable);
    }

    @Override
    public OrderResponse findById(Integer id) {
        OrderResponse response = null;
        if (id != null && id > 0) {
            response = this.orderClient.findById(HeadersUtils.generateTrafficType(), id);
        }
        return response;
    }

    @Override
    public OrderResponse save(OrderRequest order) {
        OrderResponse response = null;
        if (order != null && this.userClient.exists(HeadersUtils.generateTrafficType(), order.getUserId()) && this.productClient.exists(HeadersUtils.generateTrafficType(), order.getProductId())) {
            response = this.orderClient.save(HeadersUtils.generateTrafficType(), order);
        }
        return response;
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null && id > 0) {
            this.orderClient.deleteById(HeadersUtils.generateTrafficType(), id);
        }
    }
}
