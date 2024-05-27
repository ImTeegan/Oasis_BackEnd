package com.example.OasisBackEnd.controllers;

import com.example.OasisBackEnd.dtos.AddCustomProductItemRequest;
import com.example.OasisBackEnd.dtos.CustomProductDTO;
import com.example.OasisBackEnd.dtos.CustomProductItemDTO;
import com.example.OasisBackEnd.service.CustomProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customProduct")
public class CustomProductController {

    @Autowired
    private CustomProductService customProductService;

    @PostMapping("/create")
    public CustomProductDTO createCustomProduct(Authentication authentication) {
        return customProductService.createCustomProduct(authentication);
    }

    @GetMapping("/all")
    public List<CustomProductDTO> getAllCustomProducts() {
        return customProductService.getAllCustomProducts();
    }

    @PostMapping("/addItem")
    public CustomProductItemDTO addItemToCustomProduct(@RequestBody AddCustomProductItemRequest request) {
        return customProductService.addItemToCustomProduct(request);
    }

    @GetMapping("/{id}")
    public CustomProductDTO getCustomProductById(@PathVariable Integer id, Authentication authentication) {

        return customProductService.getCustomProductById(id, authentication);
    }

    @PutMapping("/{customProductId}/changeContextType")
    public CustomProductDTO changeCustomProductContextType(
            @PathVariable Integer customProductId,
            @RequestParam String newContextType,
            Authentication authentication) {
        return customProductService.changeCustomProductContextType(customProductId, newContextType, authentication);
    }

    @DeleteMapping("/{customProductId}/removeItem/{productId}")
    public CustomProductDTO removeItemFromCustomProduct(@PathVariable Integer customProductId, @PathVariable Integer productId, Authentication authentication) {
        return customProductService.removeItemFromCustomProduct(customProductId, productId, authentication);
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<CustomProductDTO>> getCustomProductsByOrderId(@PathVariable Integer orderId) {
        List<CustomProductDTO> customProducts = customProductService.getCustomProductsByOrderId(orderId);
        return ResponseEntity.ok(customProducts);
    }

    @DeleteMapping("/{customProductId}")
    public void deleteCustomProduct(@PathVariable Integer customProductId, Authentication authentication) {
        customProductService.deleteCustomProduct(customProductId, authentication);
    }

    @GetMapping("/shoppingCart")
    public List<CustomProductDTO> getCustomProductsByShoppingCart(Authentication authentication) {
        return customProductService.getCustomProductsByShoppingCart(authentication);
    }

    @GetMapping("/wishList")
    public List<CustomProductDTO> getCustomProductsByWishList(Authentication authentication) {
        return customProductService.getCustomProductsByWishList(authentication);
    }

    @PutMapping("/decreaseCustomProductQuantity/{customProductId}")
    @PreAuthorize("isAuthenticated()")
    public CustomProductDTO decreaseCustomProductQuantity(@PathVariable Integer customProductId, Authentication authentication) {
        return customProductService.decreaseCustomProductQuantity(customProductId, authentication);
    }

    @PutMapping("/increaseCustomProductQuantity/{customProductId}")
    @PreAuthorize("isAuthenticated()")
    public CustomProductDTO increaseCustomProductQuantity(@PathVariable Integer customProductId, Authentication authentication) {
        return customProductService.increaseCustomProductQuantity(customProductId, authentication);
    }

    @DeleteMapping("/deleteInvalid")
    // Puedes ajustar el acceso según sea necesario
    public void deleteInvalidCustomProducts() {
        customProductService.deleteInvalidCustomProducts();
    }
}
