package com.example.OasisBackEnd.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_product_shopping_carts")
public class CustomProductShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    @ManyToOne
    @JoinColumn(name = "custom_product_id", nullable = false)
    private CustomProduct customProduct;

    @Column(nullable = false)
    private Double totalCost;

    @Column(nullable = false)
    private Integer quantity;

    // Getters and setters
}
