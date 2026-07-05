package uah.es.orders.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import uah.es.orders.dto.request.user.UserRequest;

@HttpExchange("/users")
public interface IUsersClient {
    @GetExchange("/id/{id}")
    UserRequest getUserById(@RequestHeader("X-type") String version, @PathVariable Integer id);

    @GetExchange("/exists/{id}")
    Boolean exists(@RequestHeader("X-type") String version, @PathVariable Integer id);
}
