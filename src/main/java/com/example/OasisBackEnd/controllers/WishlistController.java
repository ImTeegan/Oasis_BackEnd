// File: WishlistController.java
package com.example.OasisBackEnd.controllers;

import com.example.OasisBackEnd.dtos.WishListDTO;
import com.example.OasisBackEnd.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlists")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<WishListDTO> getAllWishlists() {
        return wishlistService.getAllWishlists();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public WishListDTO getWishlistById(@PathVariable Integer id) {
        return wishlistService.getWishlistById(id);
    }

    @PostMapping
    public WishListDTO createWishlist(@RequestBody WishListDTO wishlistDTO, Authentication authentication) {
        return wishlistService.createWishlist(wishlistDTO, authentication);
    }

    @PutMapping("/{id}")
    public WishListDTO updateWishlist(@PathVariable Integer id, @RequestBody WishListDTO wishlistDTO) {
        return wishlistService.updateWishlist(id, wishlistDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void deleteWishlist(@PathVariable Integer id) {
        wishlistService.deleteWishlistById(id);
    }
}
