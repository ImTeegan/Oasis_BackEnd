package com.example.OasisBackEnd.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "wishList")
    private Set<WishListProduct> wishListProducts;

    public Set<CustomProductWishlist> getCustomProductWishlists() {
        return customProductWishlists;
    }

    public void setCustomProductWishlists(Set<CustomProductWishlist> customProductWishlists) {
        this.customProductWishlists = customProductWishlists;
    }

    public Set<WishListProduct> getWishListProducts() {
        return wishListProducts;
    }

    public void setWishListProducts(Set<WishListProduct> wishListProducts) {
        this.wishListProducts = wishListProducts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "wishList")
    private Set<CustomProductWishlist> customProductWishlists;

    // Getters and setters
}
