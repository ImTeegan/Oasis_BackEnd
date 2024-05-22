package com.example.OasisBackEnd.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "custom_products")
public class CustomProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @OneToMany(mappedBy = "customProduct")
    private List<CustomProductItem> customProductItems;

    @OneToMany(mappedBy = "customProduct")
    private List<CustomProductWishlist> customProductWishlists;

    @OneToMany(mappedBy = "customProduct")
    private List<CustomProductShoppingCart> customProductShoppingCarts;

    @OneToMany(mappedBy = "customProduct")
    private List<CustomProductOrder> customProductOrders;

    // Getters and setters
}
