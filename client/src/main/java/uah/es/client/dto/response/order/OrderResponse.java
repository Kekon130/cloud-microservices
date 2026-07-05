package uah.es.client.dto.response.order;

import uah.es.client.dto.response.product.ProductResponseMin;
import uah.es.client.dto.response.user.UserResponseMin;

import java.time.LocalDate;

public class OrderResponse {
    private Integer id;
    private UserResponseMin user;
    private ProductResponseMin product;
    private Integer quantity;
    private Float total;
    private String date;

    public OrderResponse() {
    }

    public OrderResponse(Integer id, UserResponseMin user, ProductResponseMin product, Integer quantity, Float total, String date) {
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

    public UserResponseMin getUser() {
        return user;
    }

    public void setUser(UserResponseMin user) {
        this.user = user;
    }

    public ProductResponseMin getProduct() {
        return product;
    }

    public void setProduct(ProductResponseMin product) {
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
