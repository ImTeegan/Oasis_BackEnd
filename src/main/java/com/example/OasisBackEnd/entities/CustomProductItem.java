package com.example.OasisBackEnd.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_product_items")
public class CustomProductItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "custom_product_id", nullable = false)
    private CustomProduct customProduct;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomProduct getCustomProduct() {
        return customProduct;
    }

    public void setCustomProduct(CustomProduct customProduct) {
        this.customProduct = customProduct;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
// Getters and setters
}
