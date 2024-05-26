package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDate;
import java.util.List;

public class Facade {
    public ServiceFacade serviceFacade;
    public UserFacade userFacade;
    public MarketFacade marketFacade;
    public int counter_user = 0;

    public ExternalServices externalServices;
    public UserService userService;
    public MarketService marketService;

    private boolean systemOpen;

    public Facade() {
        userFacade = new UserFacadeImp();
        userService = new UserServiceImp(userFacade);
        systemOpen = false;  // Initialize the system as closed
    }

    public String enter() {
        if (!checkSystemOpen()) {
            return "";  // Return empty string if the system is not open
        }
        String token = userService.enter(counter_user);
        counter_user++;
        // TODO Show UI
        return token;
    }

    public ResponseEntity<String> openSystem() {
        if (userService.isAdminRegistered()) {
            serviceFacade = new ServiceFacadeImp();
            externalServices = new ExternalServicesImp(serviceFacade);
            marketService = new MarketServiceImp(marketFacade);
            systemOpen = true;
            return new ResponseEntity<>("System opened successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("System cannot be opened without at least one admin registered.", HttpStatus.FORBIDDEN);
        }
    }

    // Helper method to check if the system is open
    private boolean checkSystemOpen() {
        return systemOpen;
    }

    private ResponseEntity<String> systemClosedResponse() {
        return new ResponseEntity<>("System is not open. Only registration is allowed.", HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<String> exit(String token, int id) throws Exception {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        userService.exit(id);
        Security.makeTokenExpire(token);
        return new ResponseEntity<>("User exited successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> exit(String token, String username) throws Exception {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        userService.exit(username);
        Security.makeTokenExpire(token);
        return new ResponseEntity<>("User exited successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> register(int id, String username, String password, LocalDate birthdate) {
        // Registration is allowed even if the system is not open
        try {
            userFacade.register(id, username, password, birthdate);
            return new ResponseEntity<>("User registered successfully.", HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> addService(Service service) throws InstanceAlreadyExistsException {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        serviceFacade.addService(service);
        return new ResponseEntity<>("Service added successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> replaceService(Service newService, Service oldService) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        serviceFacade.replaceService(newService, oldService);
        return new ResponseEntity<>("Service replaced successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> changeServiceName(Service serviceToChangeAt, String newName) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        serviceFacade.changeServiceName(serviceToChangeAt, newName);
        return new ResponseEntity<>("Service name changed successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> makePayment(String serviceName, double amount) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        serviceFacade.makePayment(serviceName, amount);
        return new ResponseEntity<>("Payment made successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> makeDelivery(String serviceName, String address) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        serviceFacade.makeDelivery(serviceName, address);
        return new ResponseEntity<>("Delivery made successfully.", HttpStatus.OK);
    }

    public ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                             double product_price, int product_quantity, double rating, Category category, List<String> keyWords) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
    }

    public ResponseEntity<String> removeProduct(String username, String store_name, int product_id) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.removeProduct(username, store_name, product_id);
    }

    public ResponseEntity<String> setProduct_name(String username, String store_name_id, int productId, String product_name) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setProduct_name(username, store_name_id, productId, product_name);
    }

    public ResponseEntity<String> setProduct_description(String username, String store_name_id, int productId, String product_description) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setProduct_description(username, store_name_id, productId, product_description);
    }

    public ResponseEntity<String> setProduct_price(String username, String store_name_id, int productId, int product_price) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setProduct_price(username, store_name_id, productId, product_price);
    }

    public ResponseEntity<String> setProduct_quantity(String username, String store_name_id, int productId, int product_quantity) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setProduct_quantity(username, store_name_id, productId, product_quantity);
    }

    public ResponseEntity<String> setRating(String username, String store_name_id, int productId, int rating) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setRating(username, store_name_id, productId, rating);
    }

    public ResponseEntity<String> setCategory(String username, String store_name_id, int productId, Category category) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setCategory(username, store_name_id, productId, category);
    }

    public ResponseEntity<String> login(String token, int id, String username, String password) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        if (userService.login(id, username, password)) {
            if(!token.equals(""))
                Security.makeTokenExpire(token);
            String newToken = Security.generateToken(username);
            return new ResponseEntity<>(newToken, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Login failed.", HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> logout(int id, String userName) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        userService.logout(id, userName);
        return new ResponseEntity<>("Logout successful.", HttpStatus.OK);
    }
}
