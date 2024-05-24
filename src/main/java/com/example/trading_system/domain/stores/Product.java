package com.example.trading_system.domain.stores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Product {
    private int          id;
    private String       name;
    private String       description;
    private double       price;
    private int          quantity; //@TODO move this field out side of this class - maybe Store should hold map <Product, quantity in Store>
    private double       rating;
    private Category     category;
    private List<String> keyWords;
    private String       storeName;

    public void addKeyWord(String keyWord) {
        this.keyWords.add(keyWord);
    }

    public String toString() {
        return "{" +
                "\"product_id\":" + id +
                ", \"store_name\":\"" + storeName + "\"" +
                ", \"product_name\":\"" + name + "\"" +
                ", \"product_description\":\"" + description + "\"" +
                ", \"product_price\":" + price +
                ", \"product_quantity\":" + quantity +
                ", \"rating\":" + rating +
                ", \"category\":" + (category != null ? category.toString() : "null") +
                ", \"keyWords\":" + keyWords +
                '}';
    }

}
