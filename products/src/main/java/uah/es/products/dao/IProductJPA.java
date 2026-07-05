package uah.es.products.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uah.es.products.model.Product;

public interface IProductJPA extends JpaRepository<Product, Integer> {
}