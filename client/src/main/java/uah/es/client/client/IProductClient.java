package uah.es.client.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import uah.es.client.dto.request.product.ProductRequest;
import uah.es.client.dto.response.product.ProductResponse;

import java.util.List;

@HttpExchange("/products")
public interface IProductClient {
    @GetExchange()
    List<ProductResponse> findAll(@RequestHeader("X-type") String version);

    @GetExchange("/id/{id}")
    ProductResponse findById(@RequestHeader("X-type") String version, @PathVariable Integer id);

    @GetExchange("/exists/{id}")
    Boolean exists(@RequestHeader("X-type") String version, @PathVariable Integer id);

    @PostExchange
    ProductResponse save(@RequestHeader("X-type") String version, @RequestBody ProductRequest product);

    @DeleteExchange("/id/{id}")
    void deleteById(@RequestHeader("X-type") String version, @PathVariable Integer id);
}
