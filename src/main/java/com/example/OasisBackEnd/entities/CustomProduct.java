package com.example.OasisBackEnd.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "custom_products")
public class CustomProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom_product_seq")
    @SequenceGenerator(name = "custom_product_seq", sequenceName = "custom_product_seq", allocationSize = 1, initialValue = 100000)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "context_id", nullable = false)
    private Integer contextId;

    @Enumerated(EnumType.STRING)
    @Column(name = "context_type", nullable = false)
    private ContextCustomProduct contextType;

    @Column(nullable = false)
    private Double totalCost;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer flowerCount;

    @Column(nullable = false)
    private Integer paperCount;

    @Column(nullable = false)
    private Integer foliageCount;

    @OneToMany(mappedBy = "customProduct")
    private List<CustomProductItem> customProductItems;

    public Integer getFlowerCount() {
        return flowerCount;
    }

    public void setFlowerCount(Integer flowerCount) {
        this.flowerCount = flowerCount;
    }

    public Integer getPaperCount() {
        return paperCount;
    }

    public void setPaperCount(Integer paperCount) {
        this.paperCount = paperCount;
    }

    public Integer getFoliageCount() {
        return foliageCount;
    }

    public void setFoliageCount(Integer foliageCount) {
        this.foliageCount = foliageCount;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getContextId() {
        return contextId;
    }

    public void setContextId(Integer contextId) {
        this.contextId = contextId;
    }

    public ContextCustomProduct getContextType() {
        return contextType;
    }

    public void setContextType(ContextCustomProduct contextType) {
        this.contextType = contextType;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<CustomProductItem> getCustomProductItems() {
        return customProductItems;
    }

    public void setCustomProductItems(List<CustomProductItem> customProductItems) {
        this.customProductItems = customProductItems;
    }
}
