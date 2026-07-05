package uah.es.orders.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import uah.es.orders.client.IProductsClient;
import uah.es.orders.client.IUsersClient;

@Configuration
public class RestClientConfig {
    @Value("${microservices.users.url}")
    private String usersUrl;
    @Value("${microservices.products.url}")
    private String productsUrl;

    private <T> T createClient(Class<T> clientClass, String baseUrl) {
        RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clientClass);
    }

    @Bean
    public IUsersClient usersClient() {
        return this.createClient(IUsersClient.class, this.usersUrl);
    }

    @Bean
    public IProductsClient productsClient() {
        return this.createClient(IProductsClient.class, this.productsUrl);
    }
}
