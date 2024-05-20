package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
}
