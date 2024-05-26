package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
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

    public boolean register(int id, String username, String password, LocalDate birthdate) throws Exception {
        userFacade.register(id,username,password,birthdate);
        return true;
    }

    public boolean addService(Service service) throws InstanceAlreadyExistsException {
        return serviceFacade.addService(service);
    }

    public boolean replaceService(Service newService, Service oldService){
        return serviceFacade.replaceService(newService,oldService);
    }

    public boolean changeServiceName(Service serviceToChangeAt,String newName){
        return serviceFacade.changeServiceName(serviceToChangeAt,newName);
    }

    public boolean makePayment(String serviceName,double amount){
        return serviceFacade.makePayment(serviceName,amount);
    }

    public boolean makeDelivery(String serviceName,String address){
        return serviceFacade.makeDelivery(serviceName,address);
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

    public String login(String token, int id, String username, String password){
        if (userService.login(id, username, password)){
            Security.makeTokenExpire(token);
            return Security.generateToken(username);
        }
        else
            return "";
    }

    public void logout(int id, String userName){
        userService.logout(id, userName);
//        Security.makeTokenExpire(token);
    }

}


