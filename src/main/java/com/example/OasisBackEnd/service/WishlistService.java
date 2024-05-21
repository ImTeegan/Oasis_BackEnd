// File: WishlistService.java
package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.WishListDTO;
import com.example.OasisBackEnd.entities.User;
import com.example.OasisBackEnd.entities.WishList;
import com.example.OasisBackEnd.repositories.UserRepository;
import com.example.OasisBackEnd.repositories.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishListRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);

    @Transactional
    public WishListDTO createWishlist(WishListDTO wishlistDTO, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        WishList wishlist = new WishList();
        wishlist.setUser(user);
        wishlist.setTotal(0.0);


        user.setWishlist(wishlist);

        logger.info("Saving wishlist: " + wishlist);
        WishList savedWishlist = wishlistRepository.save(wishlist);
        return convertToWishlistDTO(savedWishlist);
    }

    @Transactional
    public List<WishListDTO> getWishlistsByUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        WishList wishlist = wishlistRepository.findByUser(user);
        return List.of(convertToWishlistDTO(wishlist));
    }

    public List<WishListDTO> getAllWishlists() {
        List<WishList> wishlists = wishlistRepository.findAll();
        return wishlists.stream().map(this::convertToWishlistDTO).collect(Collectors.toList());
    }

    public WishListDTO getWishlistById(Integer id) {
        WishList wishlist = wishlistRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));
        return convertToWishlistDTO(wishlist);
    }

    public void deleteWishlistById(Integer id) {
        if (wishlistRepository.existsById(id)) {
            wishlistRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Wishlist not found");
        }
    }

    public WishListDTO updateWishlist(Integer id, WishListDTO wishlistDTO) {
        WishList wishlist = wishlistRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        if (wishlistDTO.getUserId() != null) {
            wishlist.setUser(userRepository.findById(wishlistDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found")));
        }
        wishlist.setTotal(wishlistDTO.getTotal());

        logger.info("Updating wishlist: " + wishlist);
        WishList updatedWishlist = wishlistRepository.save(wishlist);
        return convertToWishlistDTO(updatedWishlist);
    }

    private WishListDTO convertToWishlistDTO(WishList wishlist) {
        WishListDTO wishlistDTO = new WishListDTO();
        wishlistDTO.setId(wishlist.getId());
        wishlistDTO.setUserId(wishlist.getUser().getId());
        wishlistDTO.setTotal(wishlist.getTotal());

        return wishlistDTO;
    }
}
