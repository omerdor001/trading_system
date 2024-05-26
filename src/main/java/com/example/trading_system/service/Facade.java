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
            userFacade.registration(id,username,password,birthdate);
            return true;
        }
        else{
            return false;
        }
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
        return marketService.setProductName(username,store_name_id,productId,product_name);
    }
    public ResponseEntity<String> setProduct_description(String username,String store_name_id,int productId,String product_description){
        return marketService.setProductDescription(username,store_name_id,productId,product_description);
    }
    public ResponseEntity<String> setProduct_price(String username,String store_name_id,int productId,int product_price){
        return marketService.setProductPrice(username,store_name_id,productId,product_price);
    }
    public ResponseEntity<String> setProduct_quantity(String username,String store_name_id,int productId,int product_quantity){
        return marketService.setProductQuantity(username,store_name_id,productId,product_quantity);
    }
    public ResponseEntity<String> setRating(String username,String store_name_id,int productId,int rating){
        return marketService.setRating(username,store_name_id,productId,rating);
    }
    public ResponseEntity<String> setCategory(String username,String store_name_id,int productId,Category category){
        return marketService.setCategory(username,store_name_id,productId,category);
    }
    public String getAllHistoryPurchases(String userName, String storeName) {
        return marketService.getAllHistoryPurchases(userName,storeName);
    }
    public String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) {
        return marketService.getHistoryPurchasesByCustomer(userName,storeName,customerUserName);
    }

    public ResponseEntity<String> suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        return userService.suggestManage(appoint,newManager,store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);
    }

    public ResponseEntity<String> approveManager(String newManager, String store_name_id) {
        return userService.approveManage(newManager,store_name_id);
    }

    public ResponseEntity<String> appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        return userService.appointManager(appoint,newManager,store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);
    }

    public ResponseEntity<String> suggestOwner(String appoint, String newOwner, String storeName) {
        return userService.suggestOwner(appoint,newOwner,storeName);
    }

    public ResponseEntity<String> approveOwner(String newOwner, String storeName) {
        return userService.approveOwner(newOwner,storeName);
    }

    public ResponseEntity<String> appointOwner(String appoint, String newOwner, String storeName) {
        return userService.appointOwner(appoint,newOwner,storeName);
    }

    ResponseEntity<String> editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy){
        return userService.editPermissionForManager(userId, managerToEdit,storeNameId,  watch,  editSupply,  editBuyPolicy,  editDiscountPolicy);
    }
    public void openStroeExist(String storeName){
        marketFacade.openStoreExist(storeName);
    }

    public void closeStroeExist(String storeName){
        marketFacade.closeStoreExist(storeName);
    }


    public String login(int id, String username, String password){
        if (userService.login(id, username, password)){
            Security.makeTokenExpire("v" + id);
            return Security.generateToken(username);
        }
        else {
            return "";
        }
    }

}


