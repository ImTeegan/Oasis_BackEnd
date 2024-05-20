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

    // Getters and setters
}
