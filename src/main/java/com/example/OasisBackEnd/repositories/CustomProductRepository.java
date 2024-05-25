package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.ContextCustomProduct;
import com.example.OasisBackEnd.entities.CustomProduct;
import com.example.OasisBackEnd.entities.CustomProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomProductRepository extends JpaRepository<CustomProduct, Integer> {
    //List<CustomProduct> findByShoppingCartId(Integer id);

    List<CustomProduct> findByContextTypeAndContextId(ContextCustomProduct contextCustomProduct, Integer id);

    List<CustomProduct> findByContextType(ContextCustomProduct contextCustomProduct);
}
