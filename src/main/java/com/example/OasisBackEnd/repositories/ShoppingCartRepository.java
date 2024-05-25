package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.Orders;
import com.example.OasisBackEnd.entities.ShoppingCart;
import com.example.OasisBackEnd.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.OptionalInt;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    ShoppingCart findByUser(User user);

}
