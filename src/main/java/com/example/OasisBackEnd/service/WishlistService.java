// File: WishlistService.java
package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.AddProductToWishlistRequest;
import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.dtos.WishListDTO;
import com.example.OasisBackEnd.dtos.WishListProductDTO;
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
public class WishlistService {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishListProductRepository wishListProductRepository;

    @Autowired
    private ShoppingCartProductRepository shoppingCartProductRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CustomProductRepository customProductRepository;

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);

    @Transactional
    public WishListDTO createWishList(WishListDTO wishListDTO, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        WishList wishList = new WishList();
        wishList.setUser(user);
        wishList.setTotal(0.0);
        // Set other fields from wishListDTO

        user.setWishlist(wishList);

        logger.info("Saving wishlist: " + wishList);
        WishList savedWishList = wishListRepository.save(wishList);
        return convertToWishListDTO(savedWishList);
    }

    @Transactional
    public List<WishListDTO> getWishListsByUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findByUser(user);
        return List.of(convertToWishListDTO(wishList));
    }

    public List<WishListDTO> getAllWishLists() {
        List<WishList> wishLists = wishListRepository.findAll();
        return wishLists.stream().map(this::convertToWishListDTO).collect(Collectors.toList());
    }

    public WishListDTO getWishListById(Integer id) {
        WishList wishList = wishListRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));
        return convertToWishListDTO(wishList);
    }

    public void deleteWishListById(Integer id) {
        if (wishListRepository.existsById(id)) {
            wishListRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Wishlist not found");
        }
    }

    public WishListDTO updateWishList(Integer id, WishListDTO wishListDTO) {
        WishList wishList = wishListRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        if (wishListDTO.getUserId() != null) {
            wishList.setUser(userRepository.findById(wishListDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found")));
        }
        wishList.setTotal(wishListDTO.getTotal());
        // Set other fields from wishListDTO

        logger.info("Updating wishlist: " + wishList);
        WishList updatedWishList = wishListRepository.save(wishList);
        return convertToWishListDTO(updatedWishList);
    }

    @Transactional
    public WishListProductDTO addProductToWishList(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Check if the product is already in the wishlist
        Optional<WishListProduct> existingProduct = wishListProductRepository.findByWishListAndProduct(wishList, product);

        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException("Product already exists in wishlist");
        } else {
            // Create a new wishlist product entry
            WishListProduct wishListProduct = new WishListProduct();
            wishListProduct.setWishList(wishList);
            wishListProduct.setProduct(product);
            wishListProduct.setQuantity(1); // Set quantity to 1
            wishListProduct.setPrice(product.getPrice());

            wishList.setTotal(wishList.getTotal() + product.getPrice());
            wishListProductRepository.save(wishListProduct);
            wishListRepository.save(wishList);

            return convertToWishListProductDTO(wishListProduct);
        }
    }

    @Transactional
    public WishListProductDTO reduceProductQuantity(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        //Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

       // WishListProduct wishListProduct = wishListProductRepository.findByWishListAndProduct(wishList, product)
            //    .orElseThrow(() -> new IllegalArgumentException("Product not found in wishlist"));

        WishListProduct wishListProduct = wishListProductRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in wishlist"));

        if (wishListProduct.getQuantity() > 1) {
            wishListProduct.setQuantity(wishListProduct.getQuantity() - 1);
            wishList.setTotal(wishList.getTotal() - wishListProduct.getPrice());
            wishListProductRepository.save(wishListProduct);
        } else {
            throw new IllegalArgumentException("Cannot decrease quantity below 1");
        }

        return convertToWishListProductDTO(wishListProduct);
    }

    @Transactional
    public WishListProductDTO increaseProductQuantity(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        //Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

       // WishListProduct wishListProduct = wishListProductRepository.findByWishListAndProduct(wishList, product)
         //       .orElseThrow(() -> new IllegalArgumentException("Product not found in wishlist"));

        WishListProduct wishListProduct = wishListProductRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in wishlist"));

        wishListProduct.setQuantity(wishListProduct.getQuantity() + 1);
        wishList.setTotal(wishList.getTotal() + wishListProduct.getPrice());
        wishListProductRepository.save(wishListProduct);
        wishListRepository.save(wishList);

        return convertToWishListProductDTO(wishListProduct);
    }

    @Transactional
    public void removeProductFromWishList(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        WishListProduct wishListProduct = wishListProductRepository.findByWishListAndProduct(wishList, product)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in wishlist"));

        wishList.setTotal(wishList.getTotal() - (wishListProduct.getPrice() * wishListProduct.getQuantity()));
        wishList.getWishListProducts().remove(wishListProduct);
        wishListProductRepository.delete(wishListProduct);
        wishListRepository.save(wishList);
    }

    @Transactional
    public Double getTotalInWishList(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        return wishList.getTotal();
    }

    public List<WishListProductDTO> getProductsByWishList(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        List<WishListProduct> products = wishListProductRepository.findByWishList(wishList);

        return products.stream().map(this::convertToWishListProductDTO).collect(Collectors.toList());
    }

    public Double getTotalByProduct(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        WishListProduct wishListProduct = wishListProductRepository.findByWishListAndProduct(wishList, productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found")))
                .orElseThrow(() -> new IllegalArgumentException("Product not found in wishlist"));

        return wishListProduct.getQuantity() * wishListProduct.getPrice();
    }

    public Long getTotalCountProducts(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        return wishListProductRepository.countByWishList(wishList);
    }

    public Long getTotalItems(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        return wishListProductRepository.findByWishList(wishList)
                .stream()
                .mapToLong(WishListProduct::getQuantity)
                .sum();
    }

    public List<ProductDTO> getProductsInfoByWishList(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        List<WishListProduct> wishListProducts = wishListProductRepository.findByWishList(wishList);

        return wishListProducts.stream()
                .map(wishListProduct -> convertToProductDTO(wishListProduct.getProduct()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearWishList(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));
        wishList.setTotal(0.0);

        wishListProductRepository.deleteByWishList(wishList);
    }

    public boolean isProductInWishlist(Integer productId, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return wishListProductRepository.findByWishListAndProduct(wishList, product).isPresent();
    }

    @Transactional
    public void moveProductsToShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));
        ShoppingCart shoppingCart = user.getShoppingCart();

        List<WishListProduct> wishListProducts = wishListProductRepository.findByWishList(wishList);

        wishListProducts.forEach(wishListProduct -> {
            Optional<ShoppingCartProduct> existingProduct = shoppingCartProductRepository.findByShoppingCartAndProduct(shoppingCart, wishListProduct.getProduct());

            if (existingProduct.isPresent()) {
                ShoppingCartProduct shoppingCartProduct = existingProduct.get();
                shoppingCartProduct.setQuantity(shoppingCartProduct.getQuantity() + wishListProduct.getQuantity());
                shoppingCart.setTotal(shoppingCart.getTotal() + (wishListProduct.getPrice() * wishListProduct.getQuantity()));
                shoppingCartProductRepository.save(shoppingCartProduct);
            } else {
                ShoppingCartProduct shoppingCartProduct = new ShoppingCartProduct();
                shoppingCartProduct.setShoppingCart(shoppingCart);
                shoppingCartProduct.setProduct(wishListProduct.getProduct());
                shoppingCartProduct.setQuantity(wishListProduct.getQuantity());
                shoppingCartProduct.setPrice(wishListProduct.getPrice());

                shoppingCart.setTotal(shoppingCart.getTotal() + (wishListProduct.getPrice() * wishListProduct.getQuantity()));
                shoppingCartProductRepository.save(shoppingCartProduct);
            }
        });

        // Actualizar los CustomProducts relacionados con la WishList del usuario
        List<CustomProduct> customProducts = customProductRepository.findByContextTypeAndContextId(ContextCustomProduct.WISHLIST, wishList.getId());

        customProducts.forEach(customProduct -> {
            customProduct.setContextType(ContextCustomProduct.SHOPPINGCART);
            customProduct.setContextId(shoppingCart.getId());
            customProductRepository.save(customProduct);
            shoppingCart.setTotal(shoppingCart.getTotal() + (customProduct.getTotalCost()) * customProduct.getQuantity());
        });

        shoppingCartRepository.save(shoppingCart);
    }

    @Transactional
    public void moveAndDeleteProductsToShoppingCart(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishList = wishListRepository.findById(user.getWishlist().getId()).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));
        ShoppingCart shoppingCart = user.getShoppingCart();

        List<WishListProduct> wishListProducts = wishListProductRepository.findByWishList(wishList);

        wishListProducts.forEach(wishListProduct -> {
            Optional<ShoppingCartProduct> existingProduct = shoppingCartProductRepository.findByShoppingCartAndProduct(shoppingCart, wishListProduct.getProduct());

            if (existingProduct.isPresent()) {
                ShoppingCartProduct shoppingCartProduct = existingProduct.get();
                shoppingCartProduct.setQuantity(shoppingCartProduct.getQuantity() + wishListProduct.getQuantity());
                shoppingCart.setTotal(shoppingCart.getTotal() + (wishListProduct.getPrice() * wishListProduct.getQuantity()));
                shoppingCartProductRepository.save(shoppingCartProduct);
            } else {
                ShoppingCartProduct shoppingCartProduct = new ShoppingCartProduct();
                shoppingCartProduct.setShoppingCart(shoppingCart);
                shoppingCartProduct.setProduct(wishListProduct.getProduct());
                shoppingCartProduct.setQuantity(wishListProduct.getQuantity());
                shoppingCartProduct.setPrice(wishListProduct.getPrice());

                shoppingCart.setTotal(shoppingCart.getTotal() + (wishListProduct.getPrice() * wishListProduct.getQuantity()));
                shoppingCartProductRepository.save(shoppingCartProduct);
            }
        });

        List<CustomProduct> customProducts = customProductRepository.findByContextTypeAndContextId(ContextCustomProduct.WISHLIST, wishList.getId());

        customProducts.forEach(customProduct -> {
            customProduct.setContextType(ContextCustomProduct.SHOPPINGCART);
            customProduct.setContextId(shoppingCart.getId());
            customProductRepository.save(customProduct);
            shoppingCart.setTotal(shoppingCart.getTotal() + (customProduct.getTotalCost()) * customProduct.getQuantity());
        });

        shoppingCartRepository.save(shoppingCart);
        wishListProductRepository.deleteAll(wishListProducts);
        wishList.setTotal(0.0);
        wishListRepository.save(wishList);
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

    private WishListProductDTO convertToWishListProductDTO(WishListProduct wishListProduct) {
        WishListProductDTO dto = new WishListProductDTO();
        dto.setId(wishListProduct.getId());
        dto.setWishlistId(wishListProduct.getWishList().getId());
        dto.setProductId(wishListProduct.getProduct().getId());
        dto.setQuantity(wishListProduct.getQuantity());
        dto.setPrice(wishListProduct.getPrice());
        return dto;
    }

    private WishListDTO convertToWishListDTO(WishList wishList) {
        WishListDTO wishListDTO = new WishListDTO();
        wishListDTO.setId(wishList.getId());
        wishListDTO.setUserId(wishList.getUser().getId());
        wishListDTO.setTotal(wishList.getTotal());
        // Set other fields

        return wishListDTO;
    }
}
