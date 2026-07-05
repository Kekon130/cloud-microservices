package uah.es.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uah.es.client.dto.request.order.OrderRequest;
import uah.es.client.dto.response.order.OrderResponse;
import uah.es.client.paginator.PageRenderer;
import uah.es.client.service.order.IOrderService;

@Controller
@RequestMapping("/oorders")
public class OrderController {
    private IOrderService orderService;

    @Autowired
    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/new")
    public String newOrder(Model model) {
        model.addAttribute("title", "New Order");
        model.addAttribute("order", new OrderRequest());
        return "order/formOrder";
    }

    @PostMapping("/save")
    public String saveOrder(Model model, OrderRequest order, RedirectAttributes attributes) {
        this.orderService.save(order);
        attributes.addFlashAttribute("msg", "The new order has been registered successfully");
        return "redirect:/oorders/list";
    }

    @GetMapping("/list")
    public String listOrders(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<OrderResponse> orders = this.orderService.findAll(pageable);
        PageRenderer<OrderResponse> pageRenderer = new PageRenderer<OrderResponse>("/oorders/list", orders);
        model.addAttribute("title", "Orders List");
        model.addAttribute("orders", orders);
        model.addAttribute("page", pageRenderer);
        return "order/listOrders";
    }

    @GetMapping("/view/{id}")
    public String viewOrder(Model model, @PathVariable Integer id) {
        OrderResponse order = this.orderService.findById(id);
        model.addAttribute("title", "Details of the order " + order.getId());
        model.addAttribute("order", order);
        return "order/viewOrder";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(Model model, @PathVariable Integer id, RedirectAttributes attributes) {
        this.orderService.deleteById(id);
        attributes.addFlashAttribute("msg", "The order was deleted successfully");
        return "redirect:/oorders/list";
    }
}
