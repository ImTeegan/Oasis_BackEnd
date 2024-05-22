package com.example.OasisBackEnd.repositories;

import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.entities.WishList;
import com.example.OasisBackEnd.entities.WishListProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.List;

@Repository
public interface WishListProductRepository extends JpaRepository<WishListProduct, Integer> {
    Optional<WishListProduct> findByWishListAndProduct(WishList wishList, Product product);

    Long countByWishList(WishList wishList);

    List<WishListProduct> findByWishList(WishList wishList);

    void deleteByWishList(WishList wishList);
}
