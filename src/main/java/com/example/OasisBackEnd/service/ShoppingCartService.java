// File: ShoppingCartService.java
package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.AddProductToCartRequest;
import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.dtos.ShoppingCartDTO;
import com.example.OasisBackEnd.dtos.ShoppingCartProductDTO;
import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.entities.ShoppingCart;
import com.example.OasisBackEnd.entities.ShoppingCartProduct;
import com.example.OasisBackEnd.entities.User;
import com.example.OasisBackEnd.repositories.ProductRepository;
import com.example.OasisBackEnd.repositories.ShoppingCartProductRepository;
import com.example.OasisBackEnd.repositories.ShoppingCartRepository;
import com.example.OasisBackEnd.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShoppingCartProductRepository shoppingCartProductRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    @Transactional
    public ShoppingCartDTO createShoppingCart(ShoppingCartDTO shoppingCartDTO, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setTotal(0.0);
        // Set other fields from shoppingCartDTO

        user.setShoppingCart(shoppingCart);

        logger.info("Saving shopping cart: " + shoppingCart);
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return convertToShoppingCartDTO(savedShoppingCart);
    }

    @Transactional
    public List<ShoppingCartDTO> getShoppingCartsByUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user);
        return List.of(convertToShoppingCartDTO(shoppingCart));
    }

    public List<ShoppingCartDTO> getAllShoppingCarts() {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
        return shoppingCarts.stream().map(this::convertToShoppingCartDTO).collect(Collectors.toList());
    }

    public ShoppingCartDTO getShoppingCartById(Integer id) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));
        return convertToShoppingCartDTO(shoppingCart);
    }

    public void deleteShoppingCartById(Integer id) {
        if (shoppingCartRepository.existsById(id)) {
            shoppingCartRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Shopping cart not found");
        }
    }

    public ShoppingCartDTO updateShoppingCart(Integer id, ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        if (shoppingCartDTO.getUserId() != null) {
            shoppingCart.setUser(userRepository.findById(shoppingCartDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found")));
        }
        shoppingCart.setTotal(shoppingCartDTO.getTotal());
        // Set other fields from shoppingCartDTO

        logger.info("Updating shopping cart: " + shoppingCart);
        ShoppingCart updatedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return convertToShoppingCartDTO(updatedShoppingCart);
    }

    @Transactional
    public ShoppingCartProductDTO addProductToCart(AddProductToCartRequest request, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Check if the product is already in the shopping cart
        Optional<ShoppingCartProduct> existingProduct = shoppingCartProductRepository.findByShoppingCartAndProduct(shoppingCart, product);

        ShoppingCartProduct shoppingCartProduct;
        if (existingProduct.isPresent()) {
            // Update the quantity if the product already exists in the cart
            shoppingCartProduct = existingProduct.get();
            shoppingCartProduct.setQuantity(shoppingCartProduct.getQuantity() + request.getQuantity());
        } else {
            // Create a new shopping cart product entry
            shoppingCartProduct = new ShoppingCartProduct();
            shoppingCartProduct.setShoppingCart(shoppingCart);
            shoppingCartProduct.setProduct(product);
            shoppingCartProduct.setQuantity(request.getQuantity());
            shoppingCartProduct.setPrice(product.getPrice());
        }

        shoppingCartProductRepository.save(shoppingCartProduct);

        return convertToShoppingCartProductDTO(shoppingCartProduct);
    }

    @Transactional
    public ShoppingCartProductDTO reduceProductQuantity(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ShoppingCartProduct shoppingCartProduct = shoppingCartProductRepository.findByShoppingCartAndProduct(shoppingCart, product)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        if (shoppingCartProduct.getQuantity() > 1) {
            shoppingCartProduct.setQuantity(shoppingCartProduct.getQuantity() - 1);
            shoppingCartProductRepository.save(shoppingCartProduct);
        } else {
            shoppingCartProductRepository.delete(shoppingCartProduct);
        }

        return convertToShoppingCartProductDTO(shoppingCartProduct);
    }

    @Transactional
    public void removeProductFromCart(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ShoppingCartProduct shoppingCartProduct = shoppingCartProductRepository.findByShoppingCartAndProduct(shoppingCart, product)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        shoppingCartProductRepository.delete(shoppingCartProduct);
    }

    public List<ShoppingCartProductDTO> getProductsByShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        List<ShoppingCartProduct> products = shoppingCartProductRepository.findByShoppingCart(shoppingCart);

        return products.stream().map(this::convertToShoppingCartProductDTO).collect(Collectors.toList());
    }

    public Double getTotalByProduct(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        ShoppingCartProduct shoppingCartProduct = shoppingCartProductRepository.findByShoppingCartAndProduct(shoppingCart, productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found")))
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        return shoppingCartProduct.getQuantity() * shoppingCartProduct.getPrice();
    }

    public Long getTotalCountProducts(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        return shoppingCartProductRepository.countByShoppingCart(shoppingCart);
    }

    public Long getTotalItems(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        return shoppingCartProductRepository.findByShoppingCart(shoppingCart)
                .stream()
                .mapToLong(ShoppingCartProduct::getQuantity)
                .sum();
    }

    public List<ProductDTO> getProductsInfoByShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        List<ShoppingCartProduct> cartProducts = shoppingCartProductRepository.findByShoppingCart(shoppingCart);

        return cartProducts.stream()
                .map(cartProduct -> convertToProductDTO(cartProduct.getProduct()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        shoppingCartProductRepository.deleteByShoppingCart(shoppingCart);
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

    private ShoppingCartProductDTO convertToShoppingCartProductDTO(ShoppingCartProduct shoppingCartProduct) {
        ShoppingCartProductDTO dto = new ShoppingCartProductDTO();
        dto.setId(shoppingCartProduct.getId());
        dto.setShoppingCartId(shoppingCartProduct.getShoppingCart().getId());
        dto.setProductId(shoppingCartProduct.getProduct().getId());
        dto.setQuantity(shoppingCartProduct.getQuantity());
        dto.setPrice(shoppingCartProduct.getPrice());
        return dto;
    }

    private ShoppingCartDTO convertToShoppingCartDTO(ShoppingCart shoppingCart) {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setId(shoppingCart.getId());
        shoppingCartDTO.setUserId(shoppingCart.getUser().getId());
        shoppingCartDTO.setTotal(shoppingCart.getTotal());
        // Set other fields

        return shoppingCartDTO;
    }
}
