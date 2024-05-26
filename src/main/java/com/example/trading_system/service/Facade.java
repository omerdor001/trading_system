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
    public int counter_user=0;

    public ExternalServices externalServices;
    public UserService userService;
    public MarketService marketService;


    public Facade(){
        serviceFacade=new ServiceFacadeImp();
        userFacade=new UserFacadeImp();

        externalServices=new ExternalServicesImp(serviceFacade);
        userService=new UserServiceImp(userFacade);
        marketService=new MarketServiceImp(marketFacade);
    }

    public String enter(){
        String token = userService.enter(counter_user);
        counter_user++;
        //TODO Show UI
        return token;
    }


    //TODO: Do we even need that if we have logout?
    public void exit(String token, int id) throws Exception {
        userService.exit(id);
        Security.makeTokenExpire(token);
    }

    public void exit(String token, String username) throws Exception {
        userService.exit(username);
        Security.makeTokenExpire(token);
    }

    public boolean registration(String token, int id, String username, String password, LocalDate birthdate) throws Exception {
        if(Security.validateToken(token,"v"+id)){
            userFacade.register(id,username,password,birthdate);
            return true;
        }
        else{
            return false;
        }
    }

    public ResponseEntity<String> addService(String serviceName,String username,String token) {
        return externalServices.addService(serviceName);
    }

    public ResponseEntity<String> replaceService(String newServiceName, String oldServiceName,String username,String token){
        return externalServices.replaceService(newServiceName,oldServiceName);
    }

    public ResponseEntity<String> changeServiceName(String serviceToChangeAtName,String newName,String username,String token){
        return externalServices.changeServiceName(serviceToChangeAtName,newName);
    }

    public ResponseEntity<String> makePayment(String serviceName,double amount,String username,String token){
        if(Security.validateToken(token,username)) {
            return externalServices.makePayment(serviceName, amount);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> makeDelivery(String serviceName,String address,String username,String token){
        if(Security.validateToken(token,username)) {
            return externalServices.makeDelivery(serviceName, address);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                             double product_price, int product_quantity, double rating, Category category, List<String> keyWords){
        return marketService.addProduct(username,product_id,store_name,product_name,product_description,product_price,product_quantity,rating,category,keyWords);
    }

    public ResponseEntity<String> removeProduct(String username, String store_name, int product_id){
        return marketService.removeProduct(username,store_name,product_id);
    }

    public ResponseEntity<String> setProduct_name(String username, String store_name_id, int productId, String product_name){
        return marketService.setProduct_name(username,store_name_id,productId,product_name);
    }
    public ResponseEntity<String> setProduct_description(String username,String store_name_id,int productId,String product_description){
        return marketService.setProduct_description(username,store_name_id,productId,product_description);
    }
    public ResponseEntity<String> setProduct_price(String username,String store_name_id,int productId,int product_price){
        return marketService.setProduct_price(username,store_name_id,productId,product_price);
    }
    public ResponseEntity<String> setProduct_quantity(String username,String store_name_id,int productId,int product_quantity){
        return marketService.setProduct_quantity(username,store_name_id,productId,product_quantity);
    }
    public ResponseEntity<String> setRating(String username,String store_name_id,int productId,int rating){
        return marketService.setRating(username,store_name_id,productId,rating);
    }
    public ResponseEntity<String> setCategory(String username,String store_name_id,int productId,Category category){
        return marketService.setCategory(username,store_name_id,productId,category);
    }

    public String login(int id, String username, String password){
        if (userService.login(id, username, password)){
            Security.makeTokenExpire("v" + id);
            return Security.generateToken(username);
        }
        else
            return "";
    }

}


