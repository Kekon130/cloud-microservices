package uah.es.products.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uah.es.products.dao.IProductDAO;
import uah.es.products.dto.request.ProductNew;
import uah.es.products.dto.response.ProductResponse;
import uah.es.products.mapper.IProductMapper;

import java.util.List;

@Service
public class ProductService implements IProductService {
    private IProductDAO productDAO;
    private IProductMapper productMapper;

    @Autowired
    public ProductService(IProductDAO productDAO, IProductMapper productMapper) {
        this.productDAO = productDAO;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponse> findAll() {
        return this.productDAO.findAll().stream().map(this.productMapper::toProductResponse).toList();
    }

    @Override
    public ProductResponse findById(Integer id) {
        return this.productMapper.toProductResponse(this.productDAO.findById(id));
    }

    @Override
    public Boolean exists(Integer id) {
        Boolean result = false;
        if (id != null && id > 0) {
            result = this.productDAO.exists(id);
        }
        return result;
    }

    @Override
    public ProductResponse save(ProductNew productNew) {
        ProductResponse productResponse = null;
        if (productNew != null) {
            productResponse = this.productMapper.toProductResponse(this.productDAO.save(this.productMapper.toNewProduct(productNew)));
        }
        return productResponse;
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null && id > 0) {
            this.productDAO.deleteById(id);
        }
    }
}
