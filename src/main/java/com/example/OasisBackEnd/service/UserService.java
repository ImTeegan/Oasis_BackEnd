package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.RegisterUserDto;
import com.example.OasisBackEnd.dtos.UpdateUserDto;
import com.example.OasisBackEnd.dtos.UserDTO;
import com.example.OasisBackEnd.entities.Orders;
import com.example.OasisBackEnd.entities.Role;
import com.example.OasisBackEnd.entities.RoleEnum;
import com.example.OasisBackEnd.entities.User;
import com.example.OasisBackEnd.repositories.RoleRepository;
import com.example.OasisBackEnd.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users.stream().map(this::convertToUserDTO).collect(Collectors.toList());
    }

    public User createAdministrator(RegisterUserDto input) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ADMIN);

        if (optionalRole.isEmpty()) {
            return null;
        }

        var user = new User()
                .setName(input.getLastName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(optionalRole.get());

        return userRepository.save(user);
    }

    public User updateUser(Integer userId, UpdateUserDto updateUserDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (updateUserDto.getName() != null) {
                user.setName(updateUserDto.getName());
            }
            if (updateUserDto.getLastName() != null) {
                user.setLastName(updateUserDto.getLastName());
            }
            if (updateUserDto.getEmail() != null) {
                user.setEmail(updateUserDto.getEmail());
            }
            if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
            }
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return convertToUserDTO(user);
    }

    public void deleteUserById(Integer userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public UserDTO getAuthenticatedUserDTO(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        return convertToUserDTO(user);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());


        return userDTO;
    }
}
