package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.LoginUserDto;
import com.example.OasisBackEnd.dtos.RegisterUserDto;
import com.example.OasisBackEnd.entities.*;
import com.example.OasisBackEnd.exceptions.registrationUser.InvalidEmailFormat;
import com.example.OasisBackEnd.exceptions.registrationUser.InvalidPasswordFormatException;
import com.example.OasisBackEnd.exceptions.registrationUser.UserAlreadyExistsException;
import com.example.OasisBackEnd.repositories.RoleRepository;
import com.example.OasisBackEnd.repositories.ShoppingCartRepository;
import com.example.OasisBackEnd.repositories.UserRepository;
import com.example.OasisBackEnd.repositories.WishListRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final S3Service s3Service;
    private final ShoppingCartRepository shoppingCartRepository;
    private final WishListRepository wishlistRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");


    public AuthenticationService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder,
        S3Service s3Service,
        ShoppingCartRepository shoppingCartRepository,
        WishListRepository wishlistRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.shoppingCartRepository = shoppingCartRepository;
        this.wishlistRepository = wishlistRepository;
    }

    public User signup(RegisterUserDto input) {
        validateEmail(input.getEmail());
        validatePassword(input.getPassword());
        checkEmailNotUsed(input.getEmail());

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) {
            throw new IllegalArgumentException("Default role not found");
        }

        var user = new User()
                .setName(input.getName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(optionalRole.get())
                .setLastName(input.getLastName());

        User savedUser = userRepository.save(user);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(savedUser);
        shoppingCart.setTotal(0.0); // Ensure the total is set
        shoppingCartRepository.save(shoppingCart);
        savedUser.setShoppingCart(shoppingCart);

        // Create and associate Wishlist
        WishList wishlist = new WishList();
        wishlist.setUser(user);
        wishlist.setTotal(0.0);
        wishlistRepository.save(wishlist);
        savedUser.setWishlist(wishlist);

        return userRepository.save(savedUser);
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailFormat();
        }
    }

    private void validatePassword(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidPasswordFormatException();
        }
    }

    private void checkEmailNotUsed(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                input.getEmail(),
                input.getPassword()
            )
        );

        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
}
