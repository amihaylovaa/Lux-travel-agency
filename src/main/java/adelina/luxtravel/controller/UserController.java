package adelina.luxtravel.controller;

import adelina.luxtravel.domain.User;
import adelina.luxtravel.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Represents controller for a user
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

    @PostMapping(value = "/login")
    public void login(@RequestBody User user) {
        // Spring security takes care
        User user1 = new User();
        user1.getClass();
    }

    @GetMapping(value = "/username/{username}")
    public User findByUsername(@PathVariable("username") String username) {
        return userService.findByUsername(username);
    }

    @GetMapping(value = "/{id}")
    public User findById(@PathVariable("id") long id) {
        return userService.findById(id);
    }

    @GetMapping(value = "/email/{email}")
    public User findByEmail(@PathVariable("email") String email) {
        return userService.findByEmail(email);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @PatchMapping(value = "/{id}/password")
    public int updatePassword(@PathVariable("id") long id, @RequestBody User user) {
        return userService.updatePassword(user);
    }

    @PatchMapping(value = "/{id}/email")
    public User updateEmail(@PathVariable("id") long id, @RequestBody User user) {
        return userService.updateEmail(user);
    }

    @PatchMapping(value = "/{id}/role")
    public User updateRole(@RequestBody User user) {
        return userService.updateRole(user);
    }

    @DeleteMapping(value = "/{id}")
    public boolean deleteById(@PathVariable("id") long id) {
        return userService.deleteById(id);
    }

    @DeleteMapping(value = "/logout")
    public boolean logout() {
        return userService.logout();
    }
}