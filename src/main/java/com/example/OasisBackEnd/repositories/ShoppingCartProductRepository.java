package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.entities.ShoppingCart;
import com.example.OasisBackEnd.entities.ShoppingCartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProduct, Integer> {
    Optional<ShoppingCartProduct> findByShoppingCartAndProduct(ShoppingCart shoppingCart, Product product);

    List<ShoppingCartProduct> findByShoppingCart(ShoppingCart shoppingCart);

    Long countByShoppingCart(ShoppingCart shoppingCart);

    void deleteByShoppingCart(ShoppingCart shoppingCart);
}
