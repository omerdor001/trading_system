package com.example.trading_system.domain.stores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarketFacadeImp {
    private HashMap<String,Store> stores;


    public MarketFacadeImp() {
        stores = new HashMap<>();
    }

    public String getAllStores(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(", \"stores\":[");

        for (Store store : stores.values()) {
            sb.append(store.toString());
            sb.append(", ");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
    public String getStoreProducts(String store_name){
        return stores.get(store_name).toString();
    }
    public String getProductInfo(String store_name, int product_id){
        return stores.get(store_name).getProduct(product_id).toString();
    }
    public List<Product> searchNameInStore(String name,String store_name){
        return stores.get(store_name).searchName(name);
    }
    public List<Product> searchCategoryInStore(Category category,String store_name){
        return stores.get(store_name).searchCategory(category);
    }
    public List<Product> searchKeywordsInStore(String keyWords,String store_name){
        return stores.get(store_name).searchKeywords(keyWords);
    }

}
