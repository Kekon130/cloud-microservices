package uah.es.client.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uah.es.client.client.IProductClient;
import uah.es.client.dto.request.product.ProductRequest;
import uah.es.client.dto.response.product.ProductResponse;
import uah.es.client.paginator.PageUtils;
import uah.es.client.service.HeadersUtils;

@Service
public class ProductsService implements IProductsService {
    private final IProductClient productsClient;

    @Autowired
    public ProductsService(IProductClient productsClient) {
        this.productsClient = productsClient;
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable) {
        return PageUtils.toPage(this.productsClient.findAll(HeadersUtils.generateTrafficType()), pageable);
    }

    @Override
    public ProductResponse findById(Integer id) {
        ProductResponse response = null;
        if (id != null && id > 0) {
            response = this.productsClient.findById(HeadersUtils.generateTrafficType(), id);
        }
        return response;
    }

    @Override
    public ProductResponse save(ProductRequest product) {
        ProductResponse response = null;
        if (product != null) {
            response = this.productsClient.save(HeadersUtils.generateTrafficType(), product);
        }
        return response;
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null && id > 0) {
            this.productsClient.deleteById(HeadersUtils.generateTrafficType(), id);
        }
    }
}
