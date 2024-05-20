package com.example.OasisBackEnd.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_product_orders")
public class CustomProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "custom_product_id", nullable = false)
    private CustomProduct customProduct;

    @Column(nullable = false)
    private Double totalCost;

    @Column(nullable = false)
    private Integer quantity;

    // Getters and setters
}
