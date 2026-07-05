package uah.es.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uah.es.users.dto.request.UserNew;
import uah.es.users.dto.response.UserResponse;
import uah.es.users.service.IUserService;

import java.util.List;

@RestController
public class UserController {
    private IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserResponse> findAll() {
        return this.userService.findAll();
    }

    @GetMapping("/users/id/{id}")
    public UserResponse findById(@PathVariable Integer id) {
        return this.userService.findById(id);
    }

    @GetMapping("/users/exists/{id}")
    public Boolean exists(@PathVariable Integer id) {
        return this.userService.exists(id);
    }

    @PostMapping("/users")
    public UserResponse save(@RequestBody UserNew userNew) {
        return this.userService.save(userNew);
    }

    @DeleteMapping("/users/id/{id}")
    public void deleteById(@PathVariable Integer id) {
        this.userService.deleteById(id);
    }
}
