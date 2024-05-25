package com.example.OasisBackEnd.dtos;

public class AddCustomProductItemRequest {
    private Integer customProductId;
    private Integer productId;

    public Integer getCustomProductId() {
        return customProductId;
    }

    public void setCustomProductId(Integer customProductId) {
        this.customProductId = customProductId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
