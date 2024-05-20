package com.example.OasisBackEnd.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private String card;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts;

    @OneToMany(mappedBy = "order")
    private List<CustomProductOrder> customProductOrders;

    // Getters and setters
}
