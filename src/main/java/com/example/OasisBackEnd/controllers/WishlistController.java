// File: WishlistController.java
package com.example.OasisBackEnd.controllers;

import com.example.OasisBackEnd.dtos.AddProductToWishlistRequest;
import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.dtos.WishListDTO;
import com.example.OasisBackEnd.dtos.WishListProductDTO;
import com.example.OasisBackEnd.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/create")
    public WishListDTO createWishList(@RequestBody WishListDTO wishListDTO, Authentication authentication) {
        return wishlistService.createWishList(wishListDTO, authentication);
    }

    @GetMapping("/user")
    public List<WishListDTO> getWishListsByUser(Authentication authentication) {
        return wishlistService.getWishListsByUser(authentication);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<WishListDTO> getAllWishLists() {
        return wishlistService.getAllWishLists();
    }

    @GetMapping("/{id}")
    public WishListDTO getWishListById(@PathVariable Integer id) {
        return wishlistService.getWishListById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void deleteWishListById(@PathVariable Integer id) {
        wishlistService.deleteWishListById(id);
    }

    @PutMapping("/{id}")
    public WishListDTO updateWishList(@PathVariable Integer id, @RequestBody WishListDTO wishListDTO) {
        return wishlistService.updateWishList(id, wishListDTO);
    }

    @PostMapping("/addProduct/{productId}")
    public ResponseEntity<WishListProductDTO> addProductToWishList(@PathVariable Integer productId, Authentication authentication) {
        try {
            WishListProductDTO dto = wishlistService.addProductToWishList(productId, authentication);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/reduceProduct/{productId}")
    public WishListProductDTO reduceProductQuantity(@PathVariable Integer productId, Authentication authentication) {
        return wishlistService.reduceProductQuantity(productId, authentication);
    }

    @PutMapping("/increaseProduct/{productId}")
    public WishListProductDTO increaseProductQuantity(@PathVariable Integer productId, Authentication authentication) {
        return wishlistService.increaseProductQuantity(productId, authentication);
    }

    @DeleteMapping("/removeProduct/{productId}")
    public void removeProductFromWishList(@PathVariable Integer productId, Authentication authentication) {
        wishlistService.removeProductFromWishList(productId, authentication);
    }

    @GetMapping("/total")
    public Double getTotalInWishList(Authentication authentication) {
        return wishlistService.getTotalInWishList(authentication);
    }

    @GetMapping("/products")
    public List<WishListProductDTO> getProductsByWishList(Authentication authentication) {
        return wishlistService.getProductsByWishList(authentication);
    }

    @GetMapping("/isProductInWishlist/{productId}")
    public ResponseEntity<Boolean> isProductInWishlist(@PathVariable Integer productId, Authentication authentication) {
        boolean result = wishlistService.isProductInWishlist(productId, authentication);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/productTotal/{productId}")
    public Double getTotalByProduct(@PathVariable Integer productId, Authentication authentication) {
        return wishlistService.getTotalByProduct(productId, authentication);
    }

    @GetMapping("/totalCountProducts")
    public Long getTotalCountProducts(Authentication authentication) {
        return wishlistService.getTotalCountProducts(authentication);
    }

    @GetMapping("/totalItems")
    public Long getTotalItems(Authentication authentication) {
        return wishlistService.getTotalItems(authentication);
    }

    @GetMapping("/productsInfo")
    public List<ProductDTO> getProductsInfoByWishList(Authentication authentication) {
        return wishlistService.getProductsInfoByWishList(authentication);
    }

   /* @PostMapping("/moveProductsToShoppingCart")
    public ResponseEntity<Void> moveProductsToShoppingCart(Authentication authentication) {
        wishlistService.moveProductsToShoppingCart(authentication);
        return ResponseEntity.ok().build();
    }*/

    @PostMapping("/moveAndDeleteProductsToShoppingCart")
    public ResponseEntity<Void> moveAndDeleteProductsToShoppingCart(Authentication authentication) {
        wishlistService.moveAndDeleteProductsToShoppingCart(authentication);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/clear")
    public void clearWishList(Authentication authentication) {
        wishlistService.clearWishList(authentication);
    }


}
