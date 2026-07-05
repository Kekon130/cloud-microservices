package uah.es.client.service.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uah.es.client.dto.request.product.ProductRequest;
import uah.es.client.dto.response.product.ProductResponse;

public interface IProductsService {
    Page<ProductResponse> findAll(Pageable pageable);
    ProductResponse findById(Integer id);
    ProductResponse save(ProductRequest product);
    void deleteById(Integer id);
}
