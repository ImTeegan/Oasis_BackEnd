// File: AddProductToCartRequest.java
package com.example.OasisBackEnd.dtos;

public class AddProductToCartRequest {
    private Integer productId;
    private Integer quantity;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
// Getters and setters
}
