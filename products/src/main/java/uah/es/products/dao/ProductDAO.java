package uah.es.products.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uah.es.products.model.Product;

import java.util.List;

@Repository
public class ProductDAO implements IProductDAO {
    private IProductJPA productJPA;

    @Autowired
    public ProductDAO(IProductJPA productJPA) {
        this.productJPA = productJPA;
    }

    @Override
    public List<Product> findAll() {
        return this.productJPA.findAll();
    }

    @Override
    public Product findById(Integer id) {
        return this.productJPA.findById(id).orElse(null);
    }

    @Override
    public Boolean exists(Integer id) {
        return this.productJPA.existsById(id);
    }

    @Override
    public Product save(Product product) {
        return this.productJPA.save(product);
    }

    @Override
    public void deleteById(Integer id) {
        this.productJPA.deleteById(id);
    }
}
