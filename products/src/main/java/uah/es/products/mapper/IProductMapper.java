package uah.es.products.mapper;

import uah.es.products.dto.request.ProductNew;
import uah.es.products.dto.response.ProductResponse;
import uah.es.products.model.Product;

public interface IProductMapper {
    public Product toNewProduct(ProductNew productNew);
    public ProductResponse toProductResponse(Product product);
}
