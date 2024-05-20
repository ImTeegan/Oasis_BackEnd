package com.example.OasisBackEnd.controllers;

import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.service.ProductService;
import com.example.OasisBackEnd.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private S3Service s3Service;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);


    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestParam("name") String name,
                                              @RequestParam("description") String description,
                                              @RequestParam("category") String category,
                                              @RequestParam("type") String type,
                                              @RequestParam("price") double price,
                                              @RequestParam("image") MultipartFile image) throws IOException {
        logger.info("Received request to add product with name: " + name);
        String imageUrl = s3Service.uploadFile(image);
        logger.info("Image uploaded to URL: " + imageUrl);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(name);
        productDTO.setDescription(description);
        productDTO.setPrice(price);
        productDTO.setImageUrl(imageUrl);
        productDTO.setCategory(category);
        productDTO.setType(type);

        Product product = productService.createProduct(productDTO);
        logger.info("Product created with ID: " + product.getId());
        return ResponseEntity.ok(product);
    }
}
