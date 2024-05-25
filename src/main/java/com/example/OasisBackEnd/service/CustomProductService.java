package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.AddCustomProductItemRequest;
import com.example.OasisBackEnd.dtos.CustomProductDTO;
import com.example.OasisBackEnd.dtos.CustomProductItemDTO;
import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.entities.*;
import com.example.OasisBackEnd.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomProductService {

    @Autowired
    private CustomProductRepository customProductRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomProductItemRepository customProductItemRepository;

    public CustomProductDTO createCustomProduct(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        Integer contextId;

        CustomProduct customProduct = new CustomProduct();
        customProduct.setContextId(-1);
        customProduct.setContextType(ContextCustomProduct.INVALID);
        customProduct.setFlowerCount(0);
        customProduct.setFoliageCount(0);
        customProduct.setPaperCount(0);
        customProduct.setTotalCost(0.0);
        customProduct.setQuantity(1);

        CustomProduct savedCustomProduct = customProductRepository.save(customProduct);
        return convertToDTO(savedCustomProduct);
    }
/*
    public CustomProductDTO createCustomProduct(String contextType, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        Integer contextId;

        if ("ShoppingCart".equalsIgnoreCase(contextType)) {
            ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId())
                    .orElseThrow(() -> new RuntimeException("Shopping Cart not found"));
            contextId = shoppingCart.getId();
        } else if ("Wishlist".equalsIgnoreCase(contextType)) {
            WishList wishList = wishListRepository.findById(user.getWishlist().getId())
                    .orElseThrow(() -> new RuntimeException("WishList not found"));
            contextId = wishList.getId();
        } else {
            throw new IllegalArgumentException("Invalid context type");
        }

        CustomProduct customProduct = new CustomProduct();
        customProduct.setContextId(contextId);
        customProduct.setContextType(ContextCustomProduct.valueOf(contextType.toUpperCase()));
        customProduct.setFlowerCount(0);
        customProduct.setFoliageCount(0);
        customProduct.setPaperCount(0);
        customProduct.setTotalCost(0.0);
        customProduct.setQuantity(1);

        CustomProduct savedCustomProduct = customProductRepository.save(customProduct);
        return convertToDTO(savedCustomProduct);
    }*/

    @Transactional
    public List<CustomProductDTO> getAllCustomProducts() {
        List<CustomProduct> customProducts = customProductRepository.findAll();
        return customProducts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }



    public CustomProductItemDTO addItemToCustomProduct(AddCustomProductItemRequest request) {
        CustomProduct customProduct = customProductRepository.findById(request.getCustomProductId())
                .orElseThrow(() -> new RuntimeException("Custom Product not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!"item".equalsIgnoreCase(product.getType())) {
            throw new RuntimeException("Product type must be 'item'");
        }

        if ("flor".equalsIgnoreCase(product.getCategory()) && customProduct.getFlowerCount() >= 3) {
            throw new RuntimeException("Cannot add more than 3 flowers to the Custom Product");
        }

        if ("papel".equalsIgnoreCase(product.getCategory()) && customProduct.getPaperCount() >= 2) {
            throw new RuntimeException("Cannot add more than 2 paper items to the Custom Product");
        }

        if ("follaje".equalsIgnoreCase(product.getCategory()) && customProduct.getFoliageCount() >= 2) {
            throw new RuntimeException("Cannot add more than 2 foliage items to the Custom Product");
        }

        CustomProductItem customProductItem = new CustomProductItem();
        customProductItem.setCustomProduct(customProduct);
        customProductItem.setProduct(product);
        customProductItem.setPrice(product.getPrice());

        CustomProductItem savedCustomProductItem = customProductItemRepository.save(customProductItem);

        customProduct.setTotalCost(customProduct.getTotalCost() + product.getPrice());

        if ("flor".equalsIgnoreCase(product.getCategory())) {
            customProduct.setFlowerCount(customProduct.getFlowerCount() + 1);
        } else if ("papel".equalsIgnoreCase(product.getCategory())) {
            customProduct.setPaperCount(customProduct.getPaperCount() + 1);
        } else if ("follaje".equalsIgnoreCase(product.getCategory())) {
            customProduct.setFoliageCount(customProduct.getFoliageCount() + 1);
        }
        customProductRepository.save(customProduct);

        return convertToDTO(savedCustomProductItem);
    }

    public CustomProductDTO getCustomProductById(Integer id, Authentication authentication) {
        CustomProduct customProduct = customProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom Product not found"));
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));

        if (customProduct.getContextType() == ContextCustomProduct.SHOPPINGCART) {
            ShoppingCart shoppingCart = shoppingCartRepository.findById(customProduct.getContextId())
                    .orElseThrow(() -> new RuntimeException("Shopping Cart not found"));
            if (!shoppingCart.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied: Custom Product does not belong to the user's Shopping Cart");
            }
        } else if (customProduct.getContextType() == ContextCustomProduct.WISHLIST) {
            WishList wishList = wishListRepository.findById(customProduct.getContextId())
                    .orElseThrow(() -> new RuntimeException("WishList not found"));
            if (!wishList.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied: Custom Product does not belong to the user's WishList");
            }
        } else {
            throw new IllegalArgumentException("Invalid context type");
        }

        return convertToDTO(customProduct);
    }

    @Transactional
    public CustomProductDTO changeCustomProductContextType(Integer customProductId, String newContextType, Authentication authentication) {
        CustomProduct customProduct = customProductRepository.findById(customProductId)
                .orElseThrow(() -> new RuntimeException("Custom Product not found"));

        // Validar que flowerCount, paperCount y foliageCount sean al menos 1
        if (customProduct.getFlowerCount() < 1 || customProduct.getPaperCount() < 1 || customProduct.getFoliageCount() < 1) {
            throw new RuntimeException("Custom Product must have at least 1 flower, 1 paper, and 1 foliage");
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        Integer contextId;

        if ("ShoppingCart".equalsIgnoreCase(newContextType)) {
            ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId())
                    .orElseThrow(() -> new RuntimeException("Shopping Cart not found"));
            contextId = shoppingCart.getId();
            shoppingCart.setTotal(shoppingCart.getTotal() + customProduct.getTotalCost() * customProduct.getQuantity());
        } else if ("Wishlist".equalsIgnoreCase(newContextType)) {
            WishList wishList = wishListRepository.findById(user.getWishlist().getId())
                    .orElseThrow(() -> new RuntimeException("WishList not found"));
            contextId = wishList.getId();
            wishList.setTotal(wishList.getTotal() + customProduct.getTotalCost() * customProduct.getQuantity());
        } else {
            throw new IllegalArgumentException("Invalid context type");
        }



        customProduct.setContextId(contextId);
        customProduct.setContextType(ContextCustomProduct.valueOf(newContextType.toUpperCase()));

        CustomProduct updatedCustomProduct = customProductRepository.save(customProduct);
        return convertToDTO(updatedCustomProduct);
    }

    @Transactional
    public CustomProductDTO increaseCustomProductQuantity(Integer customProductId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId())
                .orElseThrow(() -> new RuntimeException("Shopping Cart not found"));

        CustomProduct customProduct = customProductRepository.findById(customProductId)
                .orElseThrow(() -> new IllegalArgumentException("Custom Product not found"));

        customProduct.setQuantity(customProduct.getQuantity() + 1);

        shoppingCart.setTotal(shoppingCart.getTotal() + customProduct.getTotalCost());

        CustomProduct updatedCustomProduct = customProductRepository.save(customProduct);
        return convertToDTO(updatedCustomProduct);
    }

    @Transactional
    public void deleteInvalidCustomProducts() {
        List<CustomProduct> invalidCustomProducts = customProductRepository.findByContextType(ContextCustomProduct.INVALID);

        for (CustomProduct customProduct : invalidCustomProducts) {
            customProductItemRepository.deleteByCustomProduct(customProduct);
            customProductRepository.delete(customProduct);
        }
    }

    @Transactional
    public CustomProductDTO decreaseCustomProductQuantity(Integer customProductId, Authentication authentication) {
        CustomProduct customProduct = customProductRepository.findById(customProductId)
                .orElseThrow(() -> new IllegalArgumentException("Custom Product not found"));
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId())
                .orElseThrow(() -> new RuntimeException("Shopping Cart not found"));

        if (customProduct.getQuantity() > 1) {
            customProduct.setQuantity(customProduct.getQuantity() - 1);
            shoppingCart.setTotal(shoppingCart.getTotal() - customProduct.getTotalCost());

        } else {
            throw new IllegalArgumentException("Cannot decrease quantity below 1");
        }

        CustomProduct updatedCustomProduct = customProductRepository.save(customProduct);
        return convertToDTO(updatedCustomProduct);
    }

    @Transactional
    public void deleteCustomProduct(Integer customProductId, Authentication authentication) {
        CustomProduct customProduct = customProductRepository.findById(customProductId)
                .orElseThrow(() -> new RuntimeException("Custom Product not found"));

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));

        // Verificar que el CustomProduct pertenece al usuario autenticado
        if (customProduct.getContextType() == ContextCustomProduct.SHOPPINGCART) {
            ShoppingCart shoppingCart = shoppingCartRepository.findById(customProduct.getContextId())
                    .orElseThrow(() -> new RuntimeException("Shopping Cart not found"));
            if (!shoppingCart.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied: Custom Product does not belong to the user's Shopping Cart");
            }
        } else if (customProduct.getContextType() == ContextCustomProduct.WISHLIST) {
            WishList wishList = wishListRepository.findById(customProduct.getContextId())
                    .orElseThrow(() -> new RuntimeException("WishList not found"));
            if (!wishList.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied: Custom Product does not belong to the user's WishList");
            }
        } else {
            throw new IllegalArgumentException("Invalid context type");
        }

        customProductItemRepository.deleteByCustomProduct(customProduct);


        customProductRepository.delete(customProduct);
    }

    @Transactional
    public List<CustomProductDTO> getCustomProductsByShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getShoppingCart().getId())
                .orElseThrow(() -> new RuntimeException("Shopping Cart not found"));

        List<CustomProduct> customProducts = customProductRepository.findByContextTypeAndContextId(ContextCustomProduct.SHOPPINGCART, shoppingCart.getId());

        return customProducts.stream().map(this::convertToDTOWithItems).collect(Collectors.toList());
    }

    @Transactional
    public List<CustomProductDTO> getCustomProductsByWishList(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId())
                .orElseThrow(() -> new RuntimeException("WishList not found"));

        List<CustomProduct> customProducts = customProductRepository.findByContextTypeAndContextId(ContextCustomProduct.WISHLIST, wishList.getId());

        return customProducts.stream().map(this::convertToDTOWithItems).collect(Collectors.toList());
    }

    private CustomProductDTO convertToDTOWithItems(CustomProduct customProduct) {
        CustomProductDTO dto = convertToDTO(customProduct);
        dto.setItems(customProduct.getCustomProductItems().stream().map(this::convertToDTOWithProduct).collect(Collectors.toList()));
        return dto;
    }

    private CustomProductItemDTO convertToDTOWithProduct(CustomProductItem customProductItem) {
        CustomProductItemDTO dto = new CustomProductItemDTO();
        dto.setId(customProductItem.getId());
        dto.setCustomProductId(customProductItem.getCustomProduct().getId());
        dto.setProduct(convertToProductDTO(customProductItem.getProduct()));
        dto.setPrice(customProductItem.getPrice());
        return dto;
    }

    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId().longValue());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setCategory(product.getCategory());
        productDTO.setType(product.getType());
        productDTO.setPrice(product.getPrice());
        productDTO.setImageUrl(product.getImageUrl());
        return productDTO;
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
}
