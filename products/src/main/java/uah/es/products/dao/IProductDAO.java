package uah.es.products.dao;

import uah.es.products.model.Product;

import java.util.List;

public interface IProductDAO {
    List<Product> findAll();
    Product findById(Integer id);
    Boolean exists(Integer id);
    Product save(Product product);
    void deleteById(Integer id);
}
