package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.CombinedOrderDTO;
import com.example.OasisBackEnd.dtos.OrderDTO;
import com.example.OasisBackEnd.dtos.OrderProductDTO;
import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.entities.*;
import com.example.OasisBackEnd.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartProductRepository shoppingCartProductRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private CustomProductRepository customProductRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ORDER_NUMBER_LENGTH = 10;
    private static final Random RANDOM = new SecureRandom();

    // Método para crear una orden
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!validateCardNumber(orderDTO.getCard())) {
            throw new IllegalArgumentException("Invalid card number");
        }

        Orders orders = new Orders();
        orders.setUser(user);
        orders.setDate(orderDTO.getDate());
        orders.setAddress1(orderDTO.getAddress1());
        orders.setAddress2(orderDTO.getAddress2());
        orders.setProvince(orderDTO.getProvince());
        orders.setCity(orderDTO.getCity());
        orders.setZipCode(orderDTO.getZipCode());
        orders.setCardHolder(orderDTO.getCardHolder());
        orders.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        orders.setCost(orderDTO.getCost());
        orders.setCard(getLast4Digits(orderDTO.getCard()));
        orders.setOrderNumber(generateOrderNumber());

        // Save the order
        Orders savedOrder = orderRepository.save(orders);

        // Retrieve products from the user's shopping cart
        ShoppingCart shoppingCart = user.getShoppingCart();
        List<ShoppingCartProduct> cartProducts = shoppingCartProductRepository.findByShoppingCart(shoppingCart);

        // Transfer products to the OrderProduct table
        List<OrderProduct> orderProducts = cartProducts.stream().map(cartProduct -> {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrders(savedOrder);
            orderProduct.setProduct(cartProduct.getProduct());
            orderProduct.setQuantity(cartProduct.getQuantity());
            orderProduct.setPrice(cartProduct.getPrice());
            return orderProduct;
        }).collect(Collectors.toList());

        orderProductRepository.saveAll(orderProducts);

        shoppingCartProductRepository.deleteAll(cartProducts);

        // Find CustomProducts with contextType SHOPPINGCART and contextId matching the user's shopping cart ID
        List<CustomProduct> customProducts = customProductRepository.findByContextTypeAndContextId(ContextCustomProduct.SHOPPINGCART, shoppingCart.getId());

        // Update contextType and contextId for the found CustomProducts
        customProducts.forEach(customProduct -> {
            customProduct.setContextType(ContextCustomProduct.ORDER);
            customProduct.setContextId(savedOrder.getId());
        });

        shoppingCart.setTotal(0.0);



        customProductRepository.saveAll(customProducts);

        return convertToOrderDTO(savedOrder);
    }

    private String generateOrderNumber() {
        StringBuilder sb = new StringBuilder(ORDER_NUMBER_LENGTH);
        for (int i = 0; i < ORDER_NUMBER_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }


    private boolean validateCardNumber(String cardNumber) {
        String visaRegex = "^4[0-9]{12}(?:[0-9]{3})?$";
        String mastercardRegex = "^5[1-5][0-9]{14}$";
        String amexRegex = "^3[47][0-9]{13}$";

        return cardNumber.matches(visaRegex) || cardNumber.matches(mastercardRegex) || cardNumber.matches(amexRegex);
    }

    private String getLast4Digits(String cardNumber) {
        if (cardNumber.length() >= 4) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }

    @Transactional
    public List<OrderDTO> getOrdersByUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Orders> orders = orderRepository.findByUser(user);
        return orders.stream().map(this::convertToOrderDTO).collect(Collectors.toList());
    }

    // Método para obtener todas las órdenes
    public List<OrderDTO> getAllOrders() {
        List<Orders> orders = new ArrayList<>();

        orderRepository.findAll().forEach(orders::add);
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener una orden por ID
    public OrderDTO getOrderById(Integer id) {
        Orders orders = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return convertToOrderDTO(orders);
    }

    // Método para eliminar una orden por ID
    @Transactional
    public void deleteOrderById(Integer id) {
        Orders orders = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        // Delete associated products
        orderProductRepository.deleteByOrders(orders);
        // Delete the order
        orderRepository.deleteById(id);
    }

    // Método para actualizar una orden por ID
    public OrderDTO updateOrder(Integer id, OrderDTO orderDTO) {
        Orders orders = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (orderDTO.getUserId() != null) {
            orders.setUser(userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found")));
        }
        if (orderDTO.getDate() != null) {
            orders.setDate(orderDTO.getDate());
        }
        if (orderDTO.getAddress1() != null) {
            orders.setAddress1(orderDTO.getAddress1());
        }
        if (orderDTO.getAddress2() != null) {
            orders.setAddress2(orderDTO.getAddress2());
        }
        if (orderDTO.getProvince() != null) {
            orders.setProvince(orderDTO.getProvince());
        }
        if (orderDTO.getCity() != null) {
            orders.setCity(orderDTO.getCity());
        }
        if (orderDTO.getZipCode() != null) {
            orders.setZipCode(orderDTO.getZipCode());
        }
        if (orderDTO.getCardHolder() != null) {
            orders.setCardHolder(orderDTO.getCardHolder());
        }
        if (orderDTO.getStatus() != null) {
            orders.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        }
        if (orderDTO.getCost() != null) {
            orders.setCost(orderDTO.getCost());
        }
        if (orderDTO.getCard() != null) {
            orders.setCard(orderDTO.getCard());
        }
        // Asigna otros campos necesarios

        logger.info("Updating order: " + orders);
        Orders updatedOrders = orderRepository.save(orders);
        return convertToOrderDTO(updatedOrders);
    }

    @Transactional
    public List<OrderProductDTO> getProductsByOrder(Integer orderId) {
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        List<OrderProduct> orderProducts = orderProductRepository.findByOrders(orders);
        return orderProducts.stream().map(this::convertToOrderProductDTO).collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDTO> getProductsInfo(Integer orderId) {
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        List<OrderProduct> orderProducts = orderProductRepository.findByOrders(orders);
        return orderProducts.stream().map(op -> convertToProductDTO(op.getProduct())).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO changeOrderStatus(Integer orderId, String status) {
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        orders.setStatus(OrderStatus.valueOf(status));
        Orders updatedOrder = orderRepository.save(orders);
        return convertToOrderDTO(updatedOrder);
    }

    @Transactional
    public List<CombinedOrderDTO> getCombinedOrdersByUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Orders> ordersList = orderRepository.findByUser(user);

        return ordersList.stream()
                .map(this::getCombinedOrderDetails)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CombinedOrderDTO> getAllCombinedOrders() {
        //List<Orders> ordersList = orderRepository.findAll();

        List<Orders> ordersList = new ArrayList<>();

        orderRepository.findAll().forEach(ordersList::add);
        return ordersList.stream()
                .map(this::getCombinedOrderDetails)
                .collect(Collectors.toList());
    }

    @Transactional
    public CombinedOrderDTO getCombinedOrderDetails(Integer orderId) {
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        List<OrderProductDTO> orderProducts = getProductsByOrder(orderId);
        List<ProductDTO> productDetails = getProductsInfo(orderId);

        CombinedOrderDTO combinedOrderDTO = new CombinedOrderDTO();
        combinedOrderDTO.setId(orders.getId());
        combinedOrderDTO.setOrderNumber(orders.getOrderNumber());
        combinedOrderDTO.setUserId(orders.getUser().getId());
        combinedOrderDTO.setDate(orders.getDate());
        combinedOrderDTO.setAddress1(orders.getAddress1());
        combinedOrderDTO.setAddress2(orders.getAddress2());
        combinedOrderDTO.setProvince(orders.getProvince());
        combinedOrderDTO.setCity(orders.getCity());
        combinedOrderDTO.setZipCode(orders.getZipCode());
        combinedOrderDTO.setCardHolder(orders.getCardHolder());
        combinedOrderDTO.setStatus(orders.getStatus().name());
        combinedOrderDTO.setCost(orders.getCost());
        combinedOrderDTO.setCard(orders.getCard());
        combinedOrderDTO.setOrderProducts(orderProducts);
        combinedOrderDTO.setProductDetails(productDetails);

        return combinedOrderDTO;
    }

    @Transactional
    public CombinedOrderDTO getCombinedOrderDetails(Orders orders) {
        Integer orderId = orders.getId();
        List<OrderProductDTO> orderProducts = getProductsByOrder(orderId);
        List<ProductDTO> productDetails = getProductsInfo(orderId);

        CombinedOrderDTO combinedOrderDTO = new CombinedOrderDTO();
        combinedOrderDTO.setId(orders.getId());
        combinedOrderDTO.setOrderNumber(orders.getOrderNumber());
        combinedOrderDTO.setUserId(orders.getUser().getId());
        combinedOrderDTO.setDate(orders.getDate());
        combinedOrderDTO.setAddress1(orders.getAddress1());
        combinedOrderDTO.setAddress2(orders.getAddress2());
        combinedOrderDTO.setProvince(orders.getProvince());
        combinedOrderDTO.setCity(orders.getCity());
        combinedOrderDTO.setZipCode(orders.getZipCode());
        combinedOrderDTO.setCardHolder(orders.getCardHolder());
        combinedOrderDTO.setStatus(orders.getStatus().name());
        combinedOrderDTO.setCost(orders.getCost());
        combinedOrderDTO.setCard(orders.getCard());
        combinedOrderDTO.setOrderProducts(orderProducts);
        combinedOrderDTO.setProductDetails(productDetails);

        return combinedOrderDTO;
    }



    private OrderDTO convertToOrderDTO(Orders orders) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orders.getId());
        orderDTO.setOrderNumber(orders.getOrderNumber());
        orderDTO.setUserId(orders.getUser().getId());
        orderDTO.setDate(orders.getDate());
        orderDTO.setAddress1(orders.getAddress1());
        orderDTO.setAddress2(orders.getAddress2());
        orderDTO.setProvince(orders.getProvince());
        orderDTO.setCity(orders.getCity());
        orderDTO.setZipCode(orders.getZipCode());
        orderDTO.setCardHolder(orders.getCardHolder());
        orderDTO.setStatus(orders.getStatus().name());
        orderDTO.setCost(orders.getCost());
        orderDTO.setCard(orders.getCard());
        // Asigna otros campos necesarios

        // Convierte los productos de la orden a DTO


        return orderDTO;
    }

    private OrderProductDTO convertToOrderProductDTO(OrderProduct orderProduct) {
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setId(orderProduct.getId());
        orderProductDTO.setOrdersId(orderProduct.getOrders().getId());
        orderProductDTO.setProductId(orderProduct.getProduct().getId());
        orderProductDTO.setQuantity(orderProduct.getQuantity());
        orderProductDTO.setPrice(orderProductDTO.getPrice());
        return orderProductDTO;
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

   /* private OrderProductDTO convertToOrderProductDTO(OrderProduct orderProduct) {
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        // Asigna los campos necesarios de orderProduct a orderProductDTO
        return orderProductDTO;
    }

    private CustomProductOrderDTO convertToCustomProductOrderDTO(CustomProductOrder customProductOrder) {
        CustomProductOrderDTO customProductOrderDTO = new CustomProductOrderDTO();
        // Asigna los campos necesarios de customProductOrder a customProductOrderDTO
        return customProductOrderDTO;
    }*/
}
