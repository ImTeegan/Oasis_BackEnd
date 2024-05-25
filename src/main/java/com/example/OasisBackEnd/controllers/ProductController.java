package com.example.OasisBackEnd.controllers;

import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.service.ProductService;
import com.example.OasisBackEnd.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private S3Service s3Service;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);


    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
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

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Método para obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // Método para eliminar un producto por ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteProductById(@PathVariable Integer id) {
        ProductDTO product = productService.getProductById(id);
        s3Service.deleteFile(product.getImageUrl());
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    // Método para actualizar un producto por ID
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ProductDTO> updateProductById(@PathVariable Integer id,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String description,
                                                        @RequestParam(required = false) String category,
                                                        @RequestParam(required = false) String type,
                                                        @RequestParam(required = false) Double price,
                                                        @RequestParam(required = false) MultipartFile image) throws IOException {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(name);
        productDTO.setDescription(description);
        productDTO.setCategory(category);
        productDTO.setType(type);
        productDTO.setPrice(price);

        if (image != null) {
            ProductDTO existingProduct = productService.getProductById(id);
            s3Service.deleteFile(existingProduct.getImageUrl());
            String imageUrl = s3Service.uploadFile(image);
            productDTO.setImageUrl(imageUrl);
        }

        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/getAllTypeProducts")
    public ResponseEntity<Page<ProductDTO>> getAllTypeProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "categories", required = false) List<String> categories,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size) {

        Page<ProductDTO> products = productService.getProducts(search, categories, sort, page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/getAllFlowerItem")
    public List<ProductDTO> getAllFlowerItem() {
        return productService.getAllCategoryProducts("Flor");
    }

    @GetMapping("/getAllPaperItem")
    public List<ProductDTO> getAllPaperItem() {
        return productService.getAllCategoryProducts("Papel");
    }

    @GetMapping("/getAllFoliageItem")
    public List<ProductDTO> getAllFoliageItem() {
        return productService.getAllCategoryProducts("Follaje");
    }
}
