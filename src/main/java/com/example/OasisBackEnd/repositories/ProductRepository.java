package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByNameContainingIgnoreCaseAndCategoryInAndType(String name, List<String> categories, String type, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndType(String name, String type, Pageable pageable);
    Page<Product> findByCategoryInAndType(List<String> categories, String type, Pageable pageable);
    Page<Product> findByType(String type, Pageable pageable);
}
