package uah.es.client.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import uah.es.client.dto.request.order.OrderRequest;
import uah.es.client.dto.response.order.OrderResponse;

import java.util.List;

@HttpExchange("/orders")
public interface IOrderClient {
    @GetExchange()
    List<OrderResponse> findAll(@RequestHeader("X-type") String version);

    @GetExchange("/id/{id}")
    OrderResponse findById(@RequestHeader("X-type") String version, @PathVariable Integer id);

    @GetExchange("/exists/{id}")
    Boolean exists(@RequestHeader("X-type") String version, @PathVariable Integer id);

    @PostExchange
    OrderResponse save(@RequestHeader("X-type") String version, @RequestBody OrderRequest order);

    @DeleteExchange("/id/{id}")
    void deleteById(@RequestHeader("X-type") String version, @PathVariable Integer id);
}
