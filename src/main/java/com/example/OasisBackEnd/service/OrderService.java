package com.example.OasisBackEnd.service;

import com.example.OasisBackEnd.dtos.OrderDTO;
import com.example.OasisBackEnd.entities.Orders;
import com.example.OasisBackEnd.entities.OrderStatus;
import com.example.OasisBackEnd.entities.Product;
import com.example.OasisBackEnd.entities.User;
import com.example.OasisBackEnd.repositories.OrderRepository;
import com.example.OasisBackEnd.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    // Método para crear una orden
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

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
        orders.setCard(orderDTO.getCard());

        user.getOrders().add(orders);
        // Asigna otros campos necesarios

        logger.info("Saving order: " + orders);
        Orders savedOrders = orderRepository.save(orders);
        return convertToOrderDTO(savedOrders);
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
    public void deleteOrderById(Integer id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Order not found");
        }
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

    private OrderDTO convertToOrderDTO(Orders orders) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orders.getId());
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