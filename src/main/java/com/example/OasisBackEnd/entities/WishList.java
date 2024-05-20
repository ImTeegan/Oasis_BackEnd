package com.example.OasisBackEnd.entities;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "wishlists")
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Double total;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "wishList")
    private Set<WishListProduct> wishListProducts;

    @OneToMany(mappedBy = "wishList")
    private Set<CustomProductWishlist> customProductWishlists;

    // Getters and setters
}
