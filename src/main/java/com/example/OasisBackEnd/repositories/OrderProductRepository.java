package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.OrderProduct;
import com.example.OasisBackEnd.entities.Orders;
import com.example.OasisBackEnd.entities.ShoppingCartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer> {
    void deleteByOrders(Orders orders);

    List<OrderProduct> findByOrders(Orders orders);
}
