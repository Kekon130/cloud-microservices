package uah.es.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uah.es.products.dto.request.ProductNew;
import uah.es.products.dto.response.ProductResponse;
import uah.es.products.service.IProductService;

import java.util.List;

@RestController
public class ProductController {
    private IProductService productService;

    @Autowired
     public ProductController(IProductService productService) {
        this.productService = productService;
     }

    @GetMapping("/products")
    public List<ProductResponse> findAll() {
         return this.productService.findAll();
    }

    @GetMapping("/products/id/{id}")
    public ProductResponse findById(@PathVariable Integer id) {
        return this.productService.findById(id);
    }

    @GetMapping("/products/exists/{id}")
    public Boolean exists(@PathVariable Integer id) {
        return this.productService.exists(id);
    }

    @PostMapping("/products")
    public ProductResponse save(@RequestBody ProductNew productNew) {
        return this.productService.save(productNew);
    }

    @DeleteMapping("/products/id/{id}")
    public void deleteById(@PathVariable Integer id) {
        this.productService.deleteById(id);
    }
}
