package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.Orders;
import com.example.OasisBackEnd.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Orders, Integer> {
    List<Orders> findByUser(User user);
}
