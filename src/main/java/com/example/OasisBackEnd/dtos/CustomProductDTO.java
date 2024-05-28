package com.example.OasisBackEnd.dtos;

import java.util.List;

public class CustomProductDTO {
    private Integer id;
    private Integer contextId;
    private String contextType;
    private String name;
    private Double totalCost;
    private Integer quantity;
    private Integer flowerCount;
    private Integer paperCount;
    private Integer foliageCount;
    private List<CustomProductItemDTO> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomProductItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CustomProductItemDTO> items) {
        this.items = items;
    }

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

    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
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
}
