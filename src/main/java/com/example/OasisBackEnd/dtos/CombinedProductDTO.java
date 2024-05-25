package com.example.OasisBackEnd.dtos;

import java.util.List;

public class CombinedProductDTO {
    private List<ShoppingCartProductDTO> shoppingCartProducts;
    private List<CustomProductDTO> customProducts;

    // Getters y Setters

    public List<ShoppingCartProductDTO> getShoppingCartProducts() {
        return shoppingCartProducts;
    }

    public void setShoppingCartProducts(List<ShoppingCartProductDTO> shoppingCartProducts) {
        this.shoppingCartProducts = shoppingCartProducts;
    }

    public List<CustomProductDTO> getCustomProducts() {
        return customProducts;
    }

    public void setCustomProducts(List<CustomProductDTO> customProducts) {
        this.customProducts = customProducts;
    }
}
