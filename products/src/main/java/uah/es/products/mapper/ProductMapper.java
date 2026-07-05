package uah.es.products.mapper;

import org.springframework.stereotype.Component;
import uah.es.products.dto.request.ProductNew;
import uah.es.products.dto.response.ProductResponse;
import uah.es.products.model.Product;

@Component
public class ProductMapper implements IProductMapper {
    @Override
    public Product toNewProduct(ProductNew productNew) {
        Product product = new Product();
        product.setId(null);
        product.setName(productNew.getName());
        product.setPrice(productNew.getPrice());
        return product;
    }

    @Override
    public ProductResponse toProductResponse(Product product) {
        if (product == null) {
            return null;
        } else {
            return new ProductResponse(
                    product.getId(),
                    product.getName().toUpperCase(),
                    product.getPrice()
            );
        }
    }
}
