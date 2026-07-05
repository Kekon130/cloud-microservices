package uah.es.client.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import uah.es.client.client.IOrderClient;
import uah.es.client.client.IProductClient;
import uah.es.client.client.IUserClient;

@Configuration
public class RestClientConfig {
    @Value("${microservices.users.url}")
    private String usersUrl;

    @Value("${microservices.products.url}")
    private String productsUrl;

    @Value("${microservices.orders.url}")
    private String ordersUrl;

    private <T> T createClient(Class<T> clientClass, String baseUrl) {
        RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clientClass);
    }

    @Bean
    public IUserClient usersClient() {
        return this.createClient(IUserClient.class, this.usersUrl);
    }

    @Bean
    public IProductClient productsClient() {
        return this.createClient(IProductClient.class, this.productsUrl);
    }

    @Bean
    public IOrderClient ordersClient() {
        return this.createClient(IOrderClient.class, this.ordersUrl);
    }
}
