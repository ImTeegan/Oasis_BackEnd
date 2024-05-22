package com.example.OasisBackEnd.controllers;

import com.example.OasisBackEnd.dtos.OrderDTO;
import com.example.OasisBackEnd.dtos.OrderProductDTO;
import com.example.OasisBackEnd.dtos.ProductDTO;
import com.example.OasisBackEnd.entities.OrderProduct;
import com.example.OasisBackEnd.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Método para obtener todas las órdenes
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // Método para obtener una orden por ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // Método para crear una nueva orden
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OrderDTO newOrder = orderService.createOrder(orderDTO, authentication);
        return ResponseEntity.ok(newOrder);
    }

    // Método para actualizar una orden por ID
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrderById(@PathVariable Integer id, @RequestBody OrderDTO orderDTO) {
        OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    // Método para eliminar una orden por ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Integer id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getMyOrders(Authentication authentication) {
        List<OrderDTO> orders = orderService.getOrdersByUser(authentication);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/products")
    public List<OrderProductDTO> getProductsByOrder(@PathVariable Integer orderId) {
        return orderService.getProductsByOrder(orderId);
    }

    @GetMapping("/{orderId}/productsInfo")
    public List<ProductDTO> getProductsInfo(@PathVariable Integer orderId) {
        return orderService.getProductsInfo(orderId);
    }

    @PutMapping("/{orderId}/changeStatus")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public OrderDTO changeOrderStatus(@PathVariable Integer orderId, @RequestParam String status) {
        return orderService.changeOrderStatus(orderId, status);
    }
}
