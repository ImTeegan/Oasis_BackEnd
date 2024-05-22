// File: ShoppingCartController.java
package com.example.OasisBackEnd.controllers;

import com.example.OasisBackEnd.dtos.AddProductToCartRequest;
import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.dtos.ShoppingCartDTO;
import com.example.OasisBackEnd.dtos.ShoppingCartProductDTO;
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

    @PostMapping("/addProduct")
    public ShoppingCartProductDTO addProductToCart(@RequestBody AddProductToCartRequest request, Authentication authentication) {
        return shoppingCartService.addProductToCart(request, authentication);
    }

    @PutMapping("/reduceProduct/{productId}")
    public ShoppingCartProductDTO reduceProductQuantity(@PathVariable Integer productId, Authentication authentication) {
        return shoppingCartService.reduceProductQuantity(productId, authentication);
    }

    @PutMapping("/increaseProduct/{productId}")
    public ShoppingCartProductDTO increaseProductQuantity(@PathVariable Integer productId, Authentication authentication) {
        return shoppingCartService.increaseProductQuantity(productId, authentication);
    }

    @DeleteMapping("/removeProduct/{productId}")
    public void removeProductFromCart(@PathVariable Integer productId, Authentication authentication) {
        shoppingCartService.removeProductFromCart(productId, authentication);
    }

    @GetMapping("/products")
    public List<ShoppingCartProductDTO> getProductsByShoppingCart(Authentication authentication) {
        return shoppingCartService.getProductsByShoppingCart(authentication);
    }

    @GetMapping("/productTotal/{productId}")
    public Double getTotalByProduct(@PathVariable Integer productId, Authentication authentication) {
        return shoppingCartService.getTotalByProduct(productId, authentication);
    }

    @GetMapping("/totalCount")
    public Long getTotalCountProducts(Authentication authentication) {
        return shoppingCartService.getTotalCountProducts(authentication);
    }

    @GetMapping("/totalItems")
    public Long getTotalItems(Authentication authentication) {
        return shoppingCartService.getTotalItems(authentication);
    }

    @GetMapping("/productsInfo")
    public List<ProductDTO> getProductsInfoByShoppingCart(Authentication authentication) {
        return shoppingCartService.getProductsInfoByShoppingCart(authentication);
    }

    @DeleteMapping("/clearAll")
    public void clearShoppingCart(Authentication authentication) {
        shoppingCartService.clearShoppingCart(authentication);
    }

    @GetMapping("/total")
    public Double getTotalInShoppingCart(Authentication authentication) {
        return shoppingCartService.getTotalInShoppingCart(authentication);
    }

}
