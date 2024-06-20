//To convert the given TradingSystem interface to receive API calls from the frontend in a Spring Boot application, you will need to create a controller class that maps HTTP requests to the methods in this interface. Here’s how you can achieve this:
//
//Create a Controller Class: Implement a new class annotated with @RestController.
//Inject the Service: Use @Autowired to inject your TradingSystem service implementation.
//Map Endpoints: Use @RequestMapping, @GetMapping, @PostMapping, etc., to map HTTP requests to methods in your controller.
//Handle Request Parameters and Bodies: Use annotations like @RequestParam, @PathVariable, and @RequestBody to handle request data.
//Here is an example of how you might start converting this:
//
//java
//Copy code
package com.example.trading_system;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.service.TradingSystemImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trading")
public class TradingSystemRestController {

    private final TradingSystemImp tradingSystem;

    public TradingSystemRestController(PaymentService paymentService, DeliveryService deliveryService, NotificationSender notificationSender){
        tradingSystem = TradingSystemImp.getInstance(paymentService, deliveryService, notificationSender);
    }

    @DeleteMapping("/instance")
    public void deleteInstance() {
        tradingSystem.deleteInstance();
    }

    @PostMapping("/openSystem")
    public ResponseEntity<String> openSystem() {
        return tradingSystem.openSystem();
    }

    @PostMapping("/closeSystem")
    public ResponseEntity<String> closeSystem(@RequestParam String username, @RequestParam String token) {
        return tradingSystem.closeSystem(username, token);
    }

    @PostMapping("/enter")
    public ResponseEntity<String> enter() {
        return tradingSystem.enter();
    }

    @PostMapping("/exit")
    public ResponseEntity<String> exit(@RequestParam String token, @RequestParam String username) {
        return tradingSystem.exit(token, username);
    }

    @GetMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password, @RequestParam LocalDate birthday) {
        return tradingSystem.register(username, password, birthday);
    }

    @PostMapping("/store/close")
    public ResponseEntity<String> closeStoreExist(@RequestParam String username, @RequestParam String token, @RequestParam String storeName) {
        return tradingSystem.closeStoreExist(username, token, storeName);
    }

    @PostMapping("/store/open")
    public ResponseEntity<String> openStoreExist(@RequestParam String username, @RequestParam String token, @RequestParam String storeName) {
        return tradingSystem.openStoreExist(username, token, storeName);
    }

    @PostMapping("/store/create")
    public ResponseEntity<String> openStore(@RequestParam String username,
                                            @RequestParam String token,
                                            @RequestParam String storeName,
                                            @RequestParam String description) {
        return tradingSystem.openStore(username, token, storeName, description );
    }

    @PostMapping("/product/add")
    public ResponseEntity<String> addProduct(@RequestParam String username, @RequestParam String token, @RequestParam int productId,
                                             @RequestParam String storeName, @RequestParam String productName,
                                             @RequestParam String productDescription, @RequestParam double productPrice,
                                             @RequestParam int productQuantity, @RequestParam double rating,
                                             @RequestParam int category, @RequestParam List<String> keyWords) {
        return tradingSystem.addProduct(username, token, productId, storeName, productName, productDescription, productPrice, productQuantity, rating, category, keyWords);
    }

    @DeleteMapping("/product/remove")
    public ResponseEntity<String> removeProduct(@RequestParam String username, @RequestParam String token, @RequestParam String storeName, @RequestParam int productId) {
        return tradingSystem.removeProduct(username, token, storeName, productId);
    }
    @GetMapping("/getStoreProducts")
    public ResponseEntity<String>getStoreProducts(@RequestParam String userName,
                                                  @RequestParam String token,
                                                  @RequestParam String store_name){
        return tradingSystem.getStoreProducts(userName, token, store_name);

    }


    // Add similar mappings for the other methods

}//package com.example.trading_system.rest;
//
//import com.example.trading_system.service.TradingSystemImp;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/getStoreProducts")
//public class restControllerStoreExist {
//
//    @PostMapping("/getStoreProducts")
//    public String storeExistence() {
//        TradingSystemImp.getInstance().getStoreProducts("lana",)
//    }
//}
