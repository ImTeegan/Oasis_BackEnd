// File: ShoppingCartService.java
package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.ShoppingCartDTO;
import com.example.OasisBackEnd.entities.ShoppingCart;
import com.example.OasisBackEnd.entities.User;
import com.example.OasisBackEnd.repositories.ShoppingCartRepository;
import com.example.OasisBackEnd.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    @Transactional
    public ShoppingCartDTO createShoppingCart(ShoppingCartDTO shoppingCartDTO, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setTotal(0.0);
        // Set other fields from shoppingCartDTO

        user.setShoppingCart(shoppingCart);

        logger.info("Saving shopping cart: " + shoppingCart);
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return convertToShoppingCartDTO(savedShoppingCart);
    }

    @Transactional
    public List<ShoppingCartDTO> getShoppingCartsByUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user);
        return List.of(convertToShoppingCartDTO(shoppingCart));
    }

    public List<ShoppingCartDTO> getAllShoppingCarts() {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
        return shoppingCarts.stream().map(this::convertToShoppingCartDTO).collect(Collectors.toList());
    }

    public ShoppingCartDTO getShoppingCartById(Integer id) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));
        return convertToShoppingCartDTO(shoppingCart);
    }

    public void deleteShoppingCartById(Integer id) {
        if (shoppingCartRepository.existsById(id)) {
            shoppingCartRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Shopping cart not found");
        }
    }

    public ShoppingCartDTO updateShoppingCart(Integer id, ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        if (shoppingCartDTO.getUserId() != null) {
            shoppingCart.setUser(userRepository.findById(shoppingCartDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found")));
        }
        shoppingCart.setTotal(shoppingCartDTO.getTotal());
        // Set other fields from shoppingCartDTO

        logger.info("Updating shopping cart: " + shoppingCart);
        ShoppingCart updatedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return convertToShoppingCartDTO(updatedShoppingCart);
    }

    private ShoppingCartDTO convertToShoppingCartDTO(ShoppingCart shoppingCart) {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setId(shoppingCart.getId());
        shoppingCartDTO.setUserId(shoppingCart.getUser().getId());
        shoppingCartDTO.setTotal(shoppingCart.getTotal());
        // Set other fields

        return shoppingCartDTO;
    }
}
