package com.example.OasisBackEnd.dtos;

import com.example.OasisBackEnd.entities.ContextCustomProduct;

public class CustomProductItemDTO {
    private Integer id;
    private Integer customProductId;
    private Integer productId;
    private Double price;


    private ProductDTO product;

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
