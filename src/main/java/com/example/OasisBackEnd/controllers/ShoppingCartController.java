// File: ShoppingCartController.java
package com.example.OasisBackEnd.controllers;

import com.example.OasisBackEnd.dtos.ShoppingCartDTO;
import com.example.OasisBackEnd.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingcart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<ShoppingCartDTO> getAllShoppingCarts() {
        return shoppingCartService.getAllShoppingCarts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ShoppingCartDTO getShoppingCartById(@PathVariable Integer id) {
        return shoppingCartService.getShoppingCartById(id);
    }

    @PostMapping
    public ShoppingCartDTO createShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO, Authentication authentication) {
        return shoppingCartService.createShoppingCart(shoppingCartDTO, authentication);
    }

    @PutMapping("/{id}")
    public ShoppingCartDTO updateShoppingCart(@PathVariable Integer id, @RequestBody ShoppingCartDTO shoppingCartDTO) {
        return shoppingCartService.updateShoppingCart(id, shoppingCartDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void deleteShoppingCart(@PathVariable Integer id) {
        shoppingCartService.deleteShoppingCartById(id);
    }
}
