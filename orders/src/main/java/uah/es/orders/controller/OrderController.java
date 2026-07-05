package uah.es.orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uah.es.orders.dto.request.order.OrderNew;
import uah.es.orders.dto.response.order.OrderResponse;
import uah.es.orders.service.IOrderService;

import java.util.List;

@RestController
public class OrderController {
    private IOrderService orderService;

    @Autowired
    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public List<OrderResponse> findAll() {
        return this.orderService.findAll();
    }

    @GetMapping("/orders/id/{id}")
    public OrderResponse findById(@PathVariable Integer id) {
        return this.orderService.findById(id);
    }

    @GetMapping("/orders/exists/{id}")
    public Boolean exists(@PathVariable Integer id) {
        return this.orderService.exists(id);
    }

    @PostMapping("/orders")
    public OrderResponse save(@RequestBody OrderNew order) {
        return this.orderService.save(order);
    }

    @DeleteMapping("/orders/id/{id}")
    public void deleteById(@PathVariable Integer id) {
        this.orderService.deleteById(id);
    }
}
