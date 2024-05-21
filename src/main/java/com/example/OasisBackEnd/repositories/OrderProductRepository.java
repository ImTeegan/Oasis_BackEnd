package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.OrderProduct;
import com.example.OasisBackEnd.entities.ShoppingCartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer> {
}
