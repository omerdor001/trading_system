package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.stores.Category;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDate;
import java.util.List;

public interface TradingSystem {
    ResponseEntity<String> openSystem();
    String enter();
    ResponseEntity<String> exit(String token, int id);
    ResponseEntity<String> exit(String token, String username);
    ResponseEntity<String> register(int id, String username, String password, LocalDate birthdate);
    ResponseEntity<String> addService(Service service);
    ResponseEntity<String> replaceService(Service newService, Service oldService);
    ResponseEntity<String> changeServiceName(Service serviceToChangeAt, String newName);
    ResponseEntity<String> makePayment(String serviceName, double amount);
    ResponseEntity<String> makeDelivery(String serviceName, String address);
    ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                      double product_price, int product_quantity, double rating, Category category, List<String> keyWords);
    ResponseEntity<String> removeProduct(String username, String store_name, int product_id);
    ResponseEntity<String> setProduct_name(String username, String store_name_id, int productId, String product_name);
    ResponseEntity<String> setProduct_description(String username, String store_name_id, int productId, String product_description);
    ResponseEntity<String> setProduct_price(String username, String store_name_id, int productId, int product_price);
    ResponseEntity<String> setProduct_quantity(String username, String store_name_id, int productId, int product_quantity);
    ResponseEntity<String> setRating(String username, String store_name_id, int productId, int rating);
    ResponseEntity<String> setCategory(String username, String store_name_id, int productId, Category category);
    ResponseEntity<String> login(String token, int id, String username, String password);
    ResponseEntity<String> logout(int id, String userName);
}
