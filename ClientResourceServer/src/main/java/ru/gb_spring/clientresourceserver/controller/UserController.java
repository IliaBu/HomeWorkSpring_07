package ru.gb_spring.clientresourceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.gb_spring.clientresourceserver.model.User;
import ru.gb_spring.clientresourceserver.service.UserService;

import java.util.List;

/**
 * The type User controller.
 */
@Controller
public class UserController {
    private final UserService userService;

    /**
     * Instantiates a new User controller.
     *
     * @param userService the user service
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Страница вывода списка пользователей
     *
     * @param model модель
     * @return список пользователей
     */
    @GetMapping("/users")
    public String findAll(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "index";
    }

    /**
     * Страница с вводом данных нового пользователя
     *
     * @param user пользователь
     * @return страницу string
     */
    @GetMapping("/user-create")
    public String createUserForm(User user) {
        return "user-create";
    }

    /**
     * Сохранение нового пользователя
     *
     * @param user пользователь
     * @return страницу со списком пользователей
     */
    @PostMapping("/user-create")
    public String createUser(User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    /**
     * Удаление пользователя по id
     *
     * @param id идентификатор пользователя
     * @return страницу со списком пользователей
     */
    @GetMapping("/user-delete/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return "redirect:/users";
    }

    /**
     * Редактирование пользователя по id
     *
     * @param model модель
     * @param id    идентификатор пользователя
     * @return страницу изменения пользователя
     */
    @GetMapping("/user-update/{id}")
    public String editUserForm(Model model, @PathVariable int id) {
        User user = userService.getUserByID(id);
        if (user == null) return "redirect:/users";
        System.out.println(user);
        model.addAttribute("user", user);
        return "user-update";
    }

    /**
     * Изменение пользователя по id
     *
     * @param user пользователь
     * @return страницу со списком пользователей
     */
    @PostMapping("/user-update/{id}")
    public String updateUser(User user) {
        userService.updateUser(user);
        return "redirect:/users";
    }
}
