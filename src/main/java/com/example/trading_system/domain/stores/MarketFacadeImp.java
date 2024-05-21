package com.example.trading_system.domain.stores;

import java.util.HashMap;

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

}
