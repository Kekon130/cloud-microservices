package uah.es.client.service.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uah.es.client.dto.request.order.OrderRequest;
import uah.es.client.dto.response.order.OrderResponse;

import java.util.List;

public interface IOrderService {
    Page<OrderResponse> findAll(Pageable pageable);
    OrderResponse findById(Integer id);
    OrderResponse save(OrderRequest order);
    void deleteById(Integer id);
}
