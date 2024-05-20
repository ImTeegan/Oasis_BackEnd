package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setCategory(productDTO.getCategory());
        product.setType(productDTO.getType());

        logger.info("Saving product: " + product);
        return productRepository.save(product);
    }
}
