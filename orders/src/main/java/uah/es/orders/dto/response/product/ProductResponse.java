package uah.es.orders.dto.response.product;

public class ProductResponse {
    private Integer id;
    private String name;

    public ProductResponse() {
    }

    public ProductResponse(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
