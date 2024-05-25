// File: ShoppingCartService.java
package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.*;
import com.example.OasisBackEnd.entities.*;
import com.example.OasisBackEnd.repositories.*;
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

    @Autowired
    private CustomProductRepository customProductRepository;

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

        shoppingCart.setTotal(shoppingCart.getTotal() + (product.getPrice() * request.getQuantity()));
        shoppingCartProductRepository.save(shoppingCartProduct);
        shoppingCartRepository.save(shoppingCart);

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
            shoppingCart.setTotal(shoppingCart.getTotal() - shoppingCartProduct.getPrice());
            shoppingCartProductRepository.save(shoppingCartProduct);
        } else {
            throw new IllegalArgumentException("Cannot decrease quantity below 1");

        }

        return convertToShoppingCartProductDTO(shoppingCartProduct);
    }

    @Transactional
    public ShoppingCartProductDTO increaseProductQuantity(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ShoppingCartProduct shoppingCartProduct = shoppingCartProductRepository.findByShoppingCartAndProduct(shoppingCart, product)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        shoppingCartProduct.setQuantity(shoppingCartProduct.getQuantity() + 1);
        shoppingCart.setTotal(shoppingCart.getTotal() + shoppingCartProduct.getPrice());
        shoppingCartProductRepository.save(shoppingCartProduct);
        shoppingCartRepository.save(shoppingCart);

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

        shoppingCart.setTotal(shoppingCart.getTotal() - (shoppingCartProduct.getPrice() * shoppingCartProduct.getQuantity()));
        shoppingCart.getShoppingCartProducts().remove(shoppingCartProduct);
        shoppingCartProductRepository.delete(shoppingCartProduct);
        shoppingCartRepository.save(shoppingCart);
    }

    @Transactional
    public Double getTotalInShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        return shoppingCart.getTotal();
    }

    public List<ShoppingCartProductDTO> getProductsByShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        List<ShoppingCartProduct> products = shoppingCartProductRepository.findByShoppingCart(shoppingCart);

        return products.stream().map(this::convertToShoppingCartProductDTOWithProduct).collect(Collectors.toList());
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

   /* @Transactional
    public List<CustomProductDTO> getValidCustomProducts(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId())
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        List<CustomProduct> customProducts = customProductRepository.findByShoppingCartId(shoppingCart.getId());

        List<CustomProductDTO> validCustomProducts = customProducts.stream()
                .filter(customProduct -> customProduct.getFlowerCount() >= 1
                        && customProduct.getFoliageCount() >= 1
                        && customProduct.getPaperCount() >= 1)
                .map(this::convertToCustomProductDTO)
                .collect(Collectors.toList());

        return validCustomProducts;
    }*/



    @Transactional
    public void clearShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));
        shoppingCart.setTotal(0.0);

        shoppingCartProductRepository.deleteByShoppingCart(shoppingCart);
    }



    public CombinedProductDTO getAllProductsGeneral(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId()).orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        List<ShoppingCartProduct> shoppingCartProducts = shoppingCartProductRepository.findByShoppingCart(shoppingCart);
        List<CustomProduct> customProducts = customProductRepository.findByContextTypeAndContextId(ContextCustomProduct.SHOPPINGCART, shoppingCart.getId());

        List<ShoppingCartProductDTO> shoppingCartProductDTOs = shoppingCartProducts.stream()
                .map(this::convertToShoppingCartProductDTOWithProduct)
                .collect(Collectors.toList());

        List<CustomProductDTO> customProductDTOs = customProducts.stream()
                .map(this::convertToDTOWithItems)
                .collect(Collectors.toList());

        CombinedProductDTO combinedProductDTO = new CombinedProductDTO();
        combinedProductDTO.setShoppingCartProducts(shoppingCartProductDTOs);
        combinedProductDTO.setCustomProducts(customProductDTOs);

        return combinedProductDTO;
    }

    private CustomProductDTO convertToDTOWithItems(CustomProduct customProduct) {
        CustomProductDTO dto = new CustomProductDTO();
        dto.setId(customProduct.getId());
        dto.setContextId(customProduct.getContextId());
        dto.setContextType(customProduct.getContextType().name());
        dto.setTotalCost(customProduct.getTotalCost());
        dto.setQuantity(customProduct.getQuantity());
        dto.setFlowerCount(customProduct.getFlowerCount());
        dto.setPaperCount(customProduct.getPaperCount());
        dto.setFoliageCount(customProduct.getFoliageCount());
        dto.setItems(customProduct.getCustomProductItems().stream().map(this::convertToItemDTO).collect(Collectors.toList()));
        return dto;
    }

    private CustomProductItemDTO convertToItemDTO(CustomProductItem customProductItem) {
        CustomProductItemDTO dto = new CustomProductItemDTO();
        dto.setId(customProductItem.getId());
        dto.setCustomProductId(customProductItem.getCustomProduct().getId());
        dto.setProduct(convertToProductDTO(customProductItem.getProduct()));
        dto.setPrice(customProductItem.getPrice());
        return dto;
    }

    public CustomProductItemDTO convertToDTO(CustomProductItem customProductItem) {
        CustomProductItemDTO dto = new CustomProductItemDTO();
        dto.setId(customProductItem.getId());
        dto.setCustomProductId(customProductItem.getCustomProduct().getId());
        dto.setProductId(customProductItem.getProduct().getId());
        dto.setPrice(customProductItem.getPrice());
        return dto;
    }

    private CustomProductDTO convertToDTO(CustomProduct customProduct) {
        CustomProductDTO dto = new CustomProductDTO();
        dto.setId(customProduct.getId());
        dto.setContextId(customProduct.getContextId());
        dto.setContextType(customProduct.getContextType().name());
        dto.setTotalCost(customProduct.getTotalCost());
        dto.setQuantity(customProduct.getQuantity());
        dto.setFlowerCount(customProduct.getFlowerCount());
        dto.setPaperCount(customProduct.getPaperCount());
        dto.setFoliageCount(customProduct.getFoliageCount());
        return dto;
    }

    private CustomProductDTO convertToCustomProductDTO(CustomProduct customProduct) {
        CustomProductDTO dto = new CustomProductDTO();
        dto.setId(customProduct.getId());
        dto.setContextId(customProduct.getContextId());
        dto.setContextType(customProduct.getContextType().name());
        dto.setTotalCost(customProduct.getTotalCost());
        dto.setQuantity(customProduct.getQuantity());
        dto.setFlowerCount(customProduct.getFlowerCount());
        dto.setPaperCount(customProduct.getPaperCount());
        dto.setFoliageCount(customProduct.getFoliageCount());
        // Añadir otros campos si es necesario
        return dto;
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

    private ShoppingCartProductDTO convertToShoppingCartProductDTOWithProduct(ShoppingCartProduct shoppingCartProduct) {
        ShoppingCartProductDTO dto = new ShoppingCartProductDTO();
        dto.setId(shoppingCartProduct.getId());
        dto.setShoppingCartId(shoppingCartProduct.getShoppingCart().getId());
        dto.setProductId(shoppingCartProduct.getProduct().getId());
        dto.setQuantity(shoppingCartProduct.getQuantity());
        dto.setPrice(shoppingCartProduct.getPrice());
        dto.setProduct(convertToProductDTO(shoppingCartProduct.getProduct()));  // Añadir detalles del producto
        return dto;
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
