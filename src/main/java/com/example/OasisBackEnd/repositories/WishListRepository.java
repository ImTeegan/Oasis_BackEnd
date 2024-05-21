package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.ShoppingCart;
import com.example.OasisBackEnd.entities.User;
import com.example.OasisBackEnd.entities.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Integer> {
    WishList findByUser(User user);
}
