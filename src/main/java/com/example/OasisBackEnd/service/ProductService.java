package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.entities.User;
import com.example.OasisBackEnd.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ProductDTO> getAllProducts() {
        List<Product> products = new ArrayList<>();

        productRepository.findAll().forEach(products::add);

        return products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener un producto por ID
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return convertToProductDTO(product);
    }


    // Método para eliminar un producto por ID
    public void deleteProductById(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }

    // Método para actualizar un producto por ID
    public ProductDTO updateProduct(Integer id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getCategory() != null) {
            product.setCategory(productDTO.getCategory());
        }
        if (productDTO.getType() != null) {
            product.setType(productDTO.getType());
        }
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getImageUrl() != null) {
            product.setImageUrl(productDTO.getImageUrl());
        }

        logger.info("Updating product: " + product);
        Product updatedProduct = productRepository.save(product);
        return convertToProductDTO(updatedProduct);
    }

    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId().longValue());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategory(product.getCategory());
        productDTO.setType(product.getType());
        productDTO.setImageUrl(product.getImageUrl());
        return productDTO;
    }
}
