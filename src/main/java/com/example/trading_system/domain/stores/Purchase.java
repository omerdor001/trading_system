package com.example.trading_system.domain.stores;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import jakarta.persistence.*;

import java.util.List;

@Getter
@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @ElementCollection
    private List<ProductInSaleDTO> productInSaleList;
    @Getter
    private String customerUsername;
    private double totalPrice;
    @Getter
    private String storeName;


    public Purchase(String customerUsername, List<ProductInSaleDTO> productInSaleList, double totalPrice, String storeName) {
        this.customerUsername = customerUsername;
        this.productInSaleList = productInSaleList;
        this.totalPrice = totalPrice;
        this.storeName = storeName;
    }

    public Purchase() {

    }

    public void addProduct(ProductInSaleDTO product) {
        productInSaleList.add(product);
        totalPrice += product.getPrice();
    }

    @Override
    public String toString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        }
        catch (JsonProcessingException e){
            throw new RuntimeException("Error converting Purchase to JSON");
        }
    }
}
