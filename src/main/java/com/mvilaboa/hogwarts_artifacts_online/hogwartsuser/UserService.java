package com.mvilaboa.hogwarts_artifacts_online.hogwartsuser;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mvilaboa.hogwarts_artifacts_online.system.exception.AlreadyInDbException;
import com.mvilaboa.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<HogwartsUser> findAll() {
        return userRepository.findAll();
    }

    public HogwartsUser findById(Integer userId) {
        return userRepository.findOneById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    public HogwartsUser save(HogwartsUser newHogwartsUser) {
        if (this.userRepository.existsByUsername(newHogwartsUser.getUsername())) {
            throw new AlreadyInDbException(
                    "user", "Username", newHogwartsUser.getUsername());
        }
        newHogwartsUser.setId(null);
        return userRepository.save(newHogwartsUser);
    }

    public HogwartsUser updateById(Integer userId, HogwartsUser user) {
        HogwartsUser userToUpdate = userRepository.findOneById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
           
        if (!userToUpdate.getUsername().equals(user.getUsername()) && 
            this.userRepository.existsByUsername(user.getUsername())) {
            throw new AlreadyInDbException(
                    "user", "Username", user.getUsername());
        }
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setEnabled(user.isEnabled());
        userToUpdate.setRoles(user.getRoles());
        return userRepository.save(userToUpdate);
    }

    public void deleteById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("user", userId);
        }
        userRepository.deleteById(userId);
    }

}
