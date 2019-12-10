package adelina.luxtravel.service;

import adelina.luxtravel.domain.User;
import adelina.luxtravel.exception.*;
import adelina.luxtravel.repository.UserRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            throw new InvalidArgumentException("Invalid username");
        }
        return getExistingUser(userRepository.findByUsername(username));
    }

    public User findByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new InvalidArgumentException("Invalid email");
        }
        return getExistingUser(userRepository.findByEmail(email));
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();

        if (ObjectUtils.isEmpty(users)) {
            throw new NonExistentItemException("There no users found");
        }
        return users;
    }

    // TODO : return result
    public void updatePassword(String newPassword, String currentPassword, String username) {
        if (StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(currentPassword)
                || StringUtils.isEmpty(username)) {
            throw new InvalidArgumentException("Update can not be executed, invalid parameters");
        }
        userRepository.updatePassword(newPassword, currentPassword, username);
    }

    // TODO : same
    public void updateEmail(String newEmail, String currentEmail) {
        if (StringUtils.isEmpty(newEmail) || StringUtils.isEmpty(currentEmail)) {
            throw new InvalidArgumentException("Update can not be executed, invalid parameters");
        }
        userRepository.updateEmail(newEmail, currentEmail);
    }

    public void deleteByUsername(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new InvalidArgumentException("Invalid username or password");
        }
        validatePasswordMatch(password, findByUsername(username));
        userRepository.deleteByUsername(username);
    }

    public void deleteByEmail(String email, String password) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            throw new InvalidArgumentException("Invalid email or password");
        }
        validatePasswordMatch(password, findByEmail(email));
        userRepository.deleteByEmail(email);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new InvalidArgumentException("Invalid user");
        }
        validateUserFields(user);
    }

    // TODO : refactor validations to be more clear
    private void validateUserFields(User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
            throw new InvalidArgumentException("Invalid User's fields");
        }
    }

    private void validatePasswordMatch(String password, User user) {
        if (!password.equals(user.getPassword())) {
            throw new InvalidArgumentException("Invalid password");
        }
    }

    // TODO : THINK AGAIN !!!!
    private User getExistingUser(User user) {
        if (user == null) {
            throw new NonExistentItemException("User does not exist");
        }
        return user;
    }
}