package com.example.OasisBackEnd.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Double total;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "shoppingCart")
    private List<ShoppingCartProduct> shoppingCartProducts;

    @OneToMany(mappedBy = "shoppingCart")
    private List<CustomProductShoppingCart> customProductShoppingCarts;

    // Getters and setters
}
