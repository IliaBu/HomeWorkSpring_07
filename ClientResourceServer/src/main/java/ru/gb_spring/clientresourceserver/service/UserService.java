package ru.gb_spring.clientresourceserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb_spring.clientresourceserver.model.User;
import ru.gb_spring.clientresourceserver.repositories.UserRepository;

import java.util.List;

/**
 * The type User service.
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    /**
     * Instantiates a new User service.
     *
     * @param userRepository the user repository
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find all list.
     *
     * @return the list
     */
    public List<User> findAll(){
        return userRepository.findAll();
    }

    /**
     * Save user.
     *
     * @param user the user
     */
    public void saveUser(User user){
        userRepository.save(user);
    }

    /**
     * Delete user by id.
     *
     * @param id the id
     */
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    /**
     * Gets user by id.
     *
     * @param id the id
     * @return the user by id
     */
    public User getUserByID(int id) {
        return userRepository.getById(id);
    }

    /**
     * Update user.
     *
     * @param user the user
     */
    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

}
