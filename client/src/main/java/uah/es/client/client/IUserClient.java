package uah.es.client.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import uah.es.client.dto.request.user.UserRequest;
import uah.es.client.dto.response.user.UserResponse;

import java.util.List;

@HttpExchange("/users")
public interface IUserClient {
    @GetExchange()
    List<UserResponse> findAll(@RequestHeader("X-type") String version);

    @GetExchange("/id/{id}")
    UserResponse findById(@RequestHeader("X-type") String version, @PathVariable Integer id);

    @GetExchange("/exists/{id}")
    Boolean exists(@RequestHeader("X-type") String version, @PathVariable Integer id);

    @PostExchange
    UserResponse save(@RequestHeader("X-type") String version, @RequestBody UserRequest user);

    @DeleteExchange("/id/{id}")
    void deleteById(@RequestHeader("X-type") String version, @PathVariable Integer id);
}
