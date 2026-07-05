package uah.es.orders.dto.response.order;

import uah.es.orders.dto.response.product.ProductResponse;
import uah.es.orders.dto.response.user.UserResponse;

import java.time.LocalDate;

public class OrderResponse {
    private Integer id;
    private UserResponse user;
    private ProductResponse product;
    private Integer quantity;
    private Float total;
    private String date;

    public OrderResponse() {
    }

    public OrderResponse(Integer id, UserResponse user, ProductResponse product, Integer quantity, Float total, String date) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.total = total;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
