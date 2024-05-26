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
            userFacade.register(id,username,password,birthdate);
            return new ResponseEntity<>("User registered successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String> addPaymentService(String serviceName,String token,String username) {
        if(Security.validateToken(token,username)) {
            return externalServices.addPaymentService(serviceName);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String> addPaymentProxyService(String serviceName,String token,String username) {
        if(Security.validateToken(token,username)) {
            return externalServices.addPaymentProxyService(serviceName);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String> addDeliveryService(String serviceName,String token,String username) {
        if(Security.validateToken(token,username)) {
            return externalServices.addDeliveryService(serviceName);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String>addDeliveryProxyService(String serviceName,String token,String username) {
        if(Security.validateToken(token,username)) {
            return externalServices.addDeliveryService(serviceName);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> replaceService(String newServiceName, String oldServiceName,String username,String token){
        if(Security.validateToken(token,username)) {
            return externalServices.replaceService(newServiceName, oldServiceName);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> changeServiceName(String serviceToChangeAtName,String newName,String username,String token) {
        if (Security.validateToken(token, username)) {
            return externalServices.changeServiceName(serviceToChangeAtName, newName);
        } else {
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);

        }
    }
    public ResponseEntity<String> makePayment(String serviceName, double amount) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        serviceFacade.makePayment(serviceName, amount);
        return new ResponseEntity<>("Payment made successfully.", HttpStatus.OK);
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
                                             double product_price, int product_quantity, double rating, int category, List<String> keyWords,String token){
        if(Security.validateToken(token,username)) {
            return marketService.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String> removeProduct(String username, String store_name, int product_id,String token){
        if(Security.validateToken(token,username)) {
            return marketService.removeProduct(username, store_name, product_id);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String> setProduct_name(String username, String store_name_id, int productId, String product_name,String token){
        if(Security.validateToken(token,username)) {
            return marketService.setProductName(username, store_name_id, productId, product_name);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String> setProduct_description(String username, String store_name_id, int productId, String product_description) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setProductDescription(username, store_name_id, productId, product_description);
    }

    public ResponseEntity<String> setProduct_price(String username,String store_name_id,int productId,int product_price,String token){
        if(Security.validateToken(token,username)) {
            return marketService.setProductPrice(username, store_name_id, productId, product_price);
        }
        else{
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<String> setProduct_quantity(String username,String store_name_id,int productId,int product_quantity,String token) {
        if (Security.validateToken(token, username)) {
            return marketService.setProductQuantity(username, store_name_id, productId, product_quantity);
        } else {
            return new ResponseEntity<>("Expired token", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> setRating(String username, String store_name_id, int productId, int rating) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setRating(username, store_name_id, productId, rating);
    }

    public ResponseEntity<String> setCategory(String username, String store_name_id, int productId, int category) {
        if (!checkSystemOpen()) {
            return systemClosedResponse();
        }
        return marketService.setCategory(username, store_name_id, productId, category);
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

    public ResponseEntity<String> approveManager(String newManager, String store_name_id, String appoint) {
        return userService.approveManage(newManager,store_name_id, appoint);
    }

//    public ResponseEntity<String> appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
//        return userService.appointManager(appoint,newManager,store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);
//    }

    public ResponseEntity<String> suggestOwner(String appoint, String newOwner, String storeName) {
        return userService.suggestOwner(appoint,newOwner,storeName);
    }

    public ResponseEntity<String> approveOwner(String newOwner, String storeName, String appoint) {
        return userService.approveOwner(newOwner,storeName, appoint);
    }

//    public ResponseEntity<String> appointOwner(String appoint, String newOwner, String storeName) {
//        return userService.appointOwner(appoint,newOwner,storeName);
//    }

    ResponseEntity<String> editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy){
        return userService.editPermissionForManager(userId, managerToEdit,storeNameId,  watch,  editSupply,  editBuyPolicy,  editDiscountPolicy);
    }
    public void openStroeExist(String storeName){
        marketFacade.openStoreExist(storeName);
    }

    public void closeStroeExist(String storeName){
        marketFacade.closeStoreExist(storeName);
    }

    String requestInformationAboutOfficialsInStore(String userName, String storeName){
        return marketService.requestInformationAboutOfficialsInStore(userName, storeName);
    }

    String requestManagersPermissions(String userName, String storeName){
        return marketService.requestManagersPermissions(userName, storeName);
    }

    String requestInformationAboutSpecificOfficialInStore(String userName, String storeName, String officialUserName){
        return marketService.requestInformationAboutSpecificOfficialInStore(userName, storeName, officialUserName);
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


