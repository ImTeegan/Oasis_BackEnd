package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.CustomProduct;
import com.example.OasisBackEnd.entities.CustomProductItem;
import com.example.OasisBackEnd.entities.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomProductItemRepository extends JpaRepository<CustomProductItem, Integer> {
    void deleteByCustomProduct(CustomProduct customProduct);
}
