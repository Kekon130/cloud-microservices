package uah.es.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uah.es.client.dto.request.product.ProductRequest;
import uah.es.client.dto.response.product.ProductResponse;
import uah.es.client.paginator.PageRenderer;
import uah.es.client.service.product.IProductsService;

@Controller
@RequestMapping("/pproducts")
public class Productcontroller {
    private IProductsService productsService;

    @Autowired
    public Productcontroller(IProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        model.addAttribute("title", "New Product");
        model.addAttribute("product", new ProductRequest());
        return "product/formProduct";
    }

    @PostMapping("/save")
    public String saveProduct(Model model, ProductRequest product, RedirectAttributes attributes) {
        this.productsService.save(product);
        attributes.addFlashAttribute("The new product was registered successfully");
        return "redirect:/pproducts/list";
    }

    @GetMapping("/list")
    public String productsList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<ProductResponse> products = this.productsService.findAll(pageable);
        PageRenderer<ProductResponse> pageRenderer = new PageRenderer<>("/pproducts/list", products);
        model.addAttribute("title", "Products List");
        model.addAttribute("products", products);
        model.addAttribute("page", pageRenderer);
        return "product/listProducts";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(Model model, @PathVariable Integer id, RedirectAttributes attributes) {
        this.productsService.deleteById(id);
        attributes.addFlashAttribute("The product was deleted successfully");
        return "redirect:/pproducts/list";
    }
}
