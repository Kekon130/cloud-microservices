package uah.es.products.service;

import uah.es.products.dto.request.ProductNew;
import uah.es.products.dto.response.ProductResponse;

import java.util.List;

public interface IProductService {
    List<ProductResponse> findAll();
    ProductResponse findById(Integer id);
    Boolean exists(Integer id);
    ProductResponse save(ProductNew productNew);
    void deleteById(Integer id);
}
