package com.example.OasisBackEnd.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "shoppingCart")
    private List<ShoppingCartProduct> shoppingCartProducts;

    @OneToMany(mappedBy = "shoppingCart")
    private List<CustomProductShoppingCart> customProductShoppingCarts;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ShoppingCartProduct> getShoppingCartProducts() {
        return shoppingCartProducts;
    }

    public void setShoppingCartProducts(List<ShoppingCartProduct> shoppingCartProducts) {
        this.shoppingCartProducts = shoppingCartProducts;
    }

    public List<CustomProductShoppingCart> getCustomProductShoppingCarts() {
        return customProductShoppingCarts;
    }

    public void setCustomProductShoppingCarts(List<CustomProductShoppingCart> customProductShoppingCarts) {
        this.customProductShoppingCarts = customProductShoppingCarts;
    }



    // Getters and setters
}
