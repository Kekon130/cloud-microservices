package uah.es.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uah.es.client.dto.request.user.UserRequest;
import uah.es.client.dto.response.user.UserResponse;
import uah.es.client.paginator.PageRenderer;
import uah.es.client.service.user.IUserService;

@Controller
@RequestMapping("/uusers")
public class UserController {
    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("title", "New User");
        model.addAttribute("user", new UserRequest());
        return "user/formUser";
    }

    @PostMapping("/save")
    public String saveUser(Model model, UserRequest user, RedirectAttributes attributes) {
        this.userService.save(user);
        attributes.addFlashAttribute("msg", "The new user was registered successfully");
        return "redirect:/uusers/list";
    }

    @GetMapping("/list")
    public String usersList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<UserResponse> users = this.userService.findAll(pageable);
        PageRenderer<UserResponse> pageRenderer = new PageRenderer<UserResponse>("/uusers/list", users);
        model.addAttribute("title","Users List");
        model.addAttribute("users", users);
        model.addAttribute("page", pageRenderer);
        return "user/listUsers";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(Model model, @PathVariable Integer id, RedirectAttributes attributes) {
        this.userService.deleteById(id);
        attributes.addFlashAttribute("msg", "The user was deleted successfully");
        return "redirect:/uusers/list";
    }
}
