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
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080") //TODO IP and Port (general)
@RequestMapping("/api/trading")
public class TradingSystemRestController {

    private final TradingSystemImp tradingSystem;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    public TradingSystemRestController(PaymentService paymentService, DeliveryService deliveryService, NotificationSender notificationSender) {
        userRepository = UserMemoryRepository.getInstance();      //TODO: change it on version 3
        storeRepository = StoreMemoryRepository.getInstance();    //TODO: change it on version 3
        tradingSystem = TradingSystemImp.getInstance(paymentService, deliveryService, notificationSender, userRepository, storeRepository);
    }

    @DeleteMapping("/instance")
    public void deleteInstance() {
        tradingSystem.deleteInstance();
    }

    @PostMapping("/openSystem")
    public ResponseEntity<String> openSystem() {
        return tradingSystem.openSystem(storeRepository);
    }

    @PostMapping("/closeSystem")
    public ResponseEntity<String> closeSystem(@RequestParam String username, @RequestParam String token) {
        return tradingSystem.closeSystem(username, token);
    }

    @GetMapping("/enter")
    public ResponseEntity<String> enter() {
        tradingSystem.openSystem(storeRepository);
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
        return tradingSystem.closeStoreExist("r"+username, token, storeName);
    }

    @PostMapping("/store/open")
    public ResponseEntity<String> openStoreExist(@RequestParam String username, @RequestParam String token, @RequestParam String storeName) {
        return tradingSystem.openStoreExist(username, token, storeName);
    }

    @PutMapping("/create-store")
    public ResponseEntity<String> openStore(@RequestParam String username,
                                            @RequestParam String token,
                                            @RequestParam String storeName,
                                            @RequestParam String description) {
        return tradingSystem.openStore("r"+username, token, storeName, description);
    }

    @PostMapping("/product/add")
    public ResponseEntity<String> addProduct(@RequestParam String username,
                                             @RequestParam String token,
                                             @RequestParam int productId,
                                             @RequestParam String storeName,
                                             @RequestParam String productName,
                                             @RequestParam String productDescription,
                                             @RequestParam double productPrice,
                                             @RequestParam int productQuantity,
                                             @RequestParam double rating,
                                             @RequestParam int category,
                                             @RequestParam String keyWords) {
        return tradingSystem.addProduct("r"+username, token, productId, storeName, productName, productDescription, productPrice, productQuantity, rating, category, keyWords);
    }

    @DeleteMapping("/product/remove")
    public ResponseEntity<String> removeProduct(@RequestParam String username,
                                                @RequestParam String token,
                                                @RequestParam String storeName,
                                                @RequestParam int productId) {
        return tradingSystem.removeProduct("r"+username, token, storeName, productId);
    }

    @GetMapping("/getStoreProducts")
    public ResponseEntity<String> getStoreProducts(@RequestParam String userName,
                                                   @RequestParam String token,
                                                   @RequestParam String store_name) {
        return tradingSystem.getStoreProducts(userName, token, store_name);

    }


    @PostMapping("/setProductName")
    public ResponseEntity<String> setProductName(@RequestParam String username,
                                                 @RequestParam String token,
                                                 @RequestParam String storeName,
                                                 @RequestParam int productId,
                                                 @RequestParam String productName) {
        return tradingSystem.setProductName("r"+username, token, storeName, productId, productName);
    }

    @PostMapping("/setProductDescription")
    public ResponseEntity<String> setProductDescription(@RequestParam String username,
                                                        @RequestParam String token,
                                                        @RequestParam String storeName,
                                                        @RequestParam int productId,
                                                        @RequestParam String productDescription) {
        return tradingSystem.setProductDescription("r"+username, token, storeName, productId, productDescription);
    }

    @PostMapping("/setProductPrice")
    public ResponseEntity<String> setProductPrice(@RequestParam String username,
                                                  @RequestParam String token,
                                                  @RequestParam String storeName,
                                                  @RequestParam int productId,
                                                  @RequestParam double productPrice) {
        return tradingSystem.setProductPrice("r"+username, token, storeName, productId, productPrice);
    }

    @PostMapping("/setProductQuantity")
    public ResponseEntity<String> setProductQuantity(@RequestParam String username,
                                                     @RequestParam String token,
                                                     @RequestParam String storeName,
                                                     @RequestParam int productId,
                                                     @RequestParam int productQuantity) {
        return tradingSystem.setProductQuantity("r"+username, token, storeName, productId, productQuantity);
    }

    @PostMapping("/setRating")
    public ResponseEntity<String> setRating(@RequestParam String username,
                                            @RequestParam String token,
                                            @RequestParam String storeName,
                                            @RequestParam int productId,
                                            @RequestParam double rating) {
        return tradingSystem.setRating("r"+username, token, storeName, productId, rating);
    }

    @PostMapping("/setCategory")
    public ResponseEntity<String> setCategory(@RequestParam String username,
                                              @RequestParam String token,
                                              @RequestParam String storeName,
                                              @RequestParam int productId,
                                              @RequestParam int category) {
        return tradingSystem.setCategory("r"+username, token, storeName, productId, category);
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String token,
                                        @RequestParam String usernameV,
                                        @RequestParam String username,
                                        @RequestParam String password) {
        return tradingSystem.login(token, usernameV, username, password);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String token, @RequestParam String username) {
        return tradingSystem.logout(token, "r"+username);
    }

    @PutMapping("/suspendUser")
    public ResponseEntity<String> suspendUser(@RequestParam String token,
                                              @RequestParam String admin,
                                              @RequestParam String toSuspend,
                                              @RequestParam LocalDateTime endSuspension) {
        return tradingSystem.suspendUser(token, "r"+admin, "r"+toSuspend, endSuspension);
    }

    @PostMapping("/endSuspendUser")
    public ResponseEntity<String> endSuspendUser(@RequestParam String token,
                                                 @RequestParam String admin,
                                                 @RequestParam String toSuspend) {
        return tradingSystem.endSuspendUser(token, "r"+admin, "r"+toSuspend);
    }

    @PostMapping("/setAddress")
    public ResponseEntity<String> setAddress(@RequestParam String username,
                                             @RequestParam String token,
                                             @RequestParam String address) {
        return tradingSystem.setAddress(username, token, address);
    }

    @GetMapping("/watchSuspensions")
    public ResponseEntity<String> watchSuspensions(@RequestParam String token, @RequestParam String admin) {
        return tradingSystem.watchSuspensions(token, "r"+admin);
    }

    @PostMapping("/suggestOwner")
    public ResponseEntity<String> suggestOwner(@RequestParam String appoint,
                                               @RequestParam String token,
                                               @RequestParam String newOwner,
                                               @RequestParam String storeName) {
        return tradingSystem.suggestOwner("r"+appoint, token, "r"+newOwner, storeName);
    }

    @PostMapping("/suggestManage")
    public ResponseEntity<String> suggestManage(@RequestParam String appoint,
                                                @RequestParam String token,
                                                @RequestParam String newManager,
                                                @RequestParam String store_name_id,
                                                @RequestParam boolean watch,
                                                @RequestParam boolean editSupply,
                                                @RequestParam boolean editBuyPolicy,
                                                @RequestParam boolean editDiscountPolicy) {
        return tradingSystem.suggestManage("r"+appoint, token, "r"+newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @PostMapping("/approveManage")
    public ResponseEntity<String> approveManage(@RequestParam String newManager,
                                                @RequestParam String token,
                                                @RequestParam String store_name_id,
                                                @RequestParam String appoint,
                                                @RequestParam boolean watch,
                                                @RequestParam boolean editSupply,
                                                @RequestParam boolean editBuyPolicy,
                                                @RequestParam boolean editDiscountPolicy) {
        return tradingSystem.approveManage("r"+newManager, token, store_name_id, "r"+appoint,watch,editSupply,editBuyPolicy,editDiscountPolicy);
    }

    @PostMapping("/approveOwner")
    public ResponseEntity<String> approveOwner(@RequestParam String newOwner,
                                               @RequestParam String token,
                                               @RequestParam String storeName,
                                               @RequestParam String appoint) {
        return tradingSystem.approveOwner("r"+newOwner, token, storeName, "r"+appoint);
    }

    @PostMapping("/rejectToOwnStore")
    public ResponseEntity<String> rejectToOwnStore(@RequestParam String username,
                                                   @RequestParam String token,
                                                   @RequestParam String storeName,
                                                   @RequestParam String appoint) {
        return tradingSystem.rejectToOwnStore("r"+username, token, storeName, "r"+appoint);
    }

    @PostMapping("/rejectToManageStore")
    public ResponseEntity<String> rejectToManageStore(@RequestParam String username,
                                                      @RequestParam String token,
                                                      @RequestParam String store_name_id,
                                                      @RequestParam String appoint) {
        return tradingSystem.rejectToManageStore("r"+username, token, store_name_id, "r"+appoint);
    }

    @PostMapping("/waiverOnOwnership")
    public ResponseEntity<String> waiverOnOwnership(@RequestParam String username,
                                                    @RequestParam String token,
                                                    @RequestParam String storeName) {
        return tradingSystem.waiverOnOwnership("r"+username, token, storeName);
    }

    @PostMapping("/fireManager")
    public ResponseEntity<String> fireManager(@RequestParam String owner,
                                              @RequestParam String token,
                                              @RequestParam String storeName,
                                              @RequestParam String manager) {
        return tradingSystem.fireManager(owner, token, storeName, manager);
    }

    @PostMapping("/fireOwner")
    public ResponseEntity<String> fireOwner(@RequestParam String ownerAppoint,
                                            @RequestParam String token,
                                            @RequestParam String storeName,
                                            @RequestParam String ownerToFire) {
        return tradingSystem.fireOwner(ownerAppoint, token, storeName, ownerToFire);
    }

    @PostMapping("/store/manager/permission/edit")
    public ResponseEntity<String> editPermissionForManager(@RequestParam String username,
                                                           @RequestParam String token,
                                                           @RequestParam String managerToEdit,
                                                           @RequestParam String storeNameId,
                                                           @RequestParam boolean watch,
                                                           @RequestParam boolean editSupply,
                                                           @RequestParam boolean editBuyPolicy,
                                                           @RequestParam boolean editDiscountPolicy) {
        return tradingSystem.editPermissionForManager(username, token, managerToEdit, storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @GetMapping("/stores")
    public ResponseEntity<String> getAllStores(@RequestParam String userName, @RequestParam String token) {
        return tradingSystem.getAllStores("r"+ userName, token);
    }

    @GetMapping("/stores-detailed-info")
    public ResponseEntity<String> getAllStoresInJSONFormat(@RequestParam String username, @RequestParam String token) {
        return tradingSystem.getAllStoresInJSONFormat("r"+username, token);
    }

    @GetMapping("/products_of_store")
    public ResponseEntity<String> getProductsFromStoreJSONFormat(@RequestParam String storeName,@RequestParam String username, @RequestParam String token) {
        return tradingSystem.getProductsFromStoreJSONFormat(storeName,"r"+username,token);
    }

    @GetMapping("/stores-I-created")
    public ResponseEntity<String> getStoresIOpened(@RequestParam String userName, @RequestParam String token) {
        return tradingSystem.getStoresIOpened("r"+userName,token);
    }

    @GetMapping("/stores-I-own")
    public ResponseEntity<String> getStoresIOwn(@RequestParam String userName, @RequestParam String token) {
        return tradingSystem.getStoresIOwn("r"+userName,token);
    }

    @GetMapping("/stores-I-manage")
    public ResponseEntity<String> getStoresIManage(@RequestParam String userName, @RequestParam String token) {
        return tradingSystem.getStoresIManage("r"+userName,token);
    }

    @GetMapping("/requests-for-ownership")
    public ResponseEntity<String> getUserRequestsOwnership(@RequestParam String username, @RequestParam String token) {
        return tradingSystem.getUserRequestsOwnership("r"+username,token);
    }

    @GetMapping("/requests-for-management")
    public ResponseEntity<String> getUserRequestsManagement(@RequestParam String username, @RequestParam String token) {
        return tradingSystem.getUserRequestsManagement("r"+username,token);
    }

    @GetMapping("/permissions-for-user")
    public ResponseEntity<String> getPermissionsForUserJSONFormat(@RequestParam String username,@RequestParam String storeName, @RequestParam String token) {
        return tradingSystem.getPermissionsForUserJSONFormat("r"+username,token,storeName);
    }

    @GetMapping("/product/info")
    public ResponseEntity<String> getProductInfo(@RequestParam String userName, @RequestParam String token,
                                                 @RequestParam String storeName, @RequestParam int product_Id) {
        return tradingSystem.getProductInfo(userName, token, storeName, product_Id);
    }

    @GetMapping("/store/search/name")
    public ResponseEntity<String> searchNameInStore(@RequestParam String userName, @RequestParam String productName, @RequestParam String token,
                                                    @RequestParam String store_name, @RequestParam Double minPrice, @RequestParam Double maxPrice,
                                                    @RequestParam Double minRating, @RequestParam int category) {
        return tradingSystem.searchNameInStore(userName, productName, token, store_name, minPrice, maxPrice, minRating, category);
    }

    @GetMapping("/store/search/category")
    public ResponseEntity<String> searchCategoryInStore(@RequestParam String userName, @RequestParam String token, @RequestParam int category,
                                                        @RequestParam String store_name, @RequestParam Double minPrice, @RequestParam Double maxPrice,
                                                        @RequestParam Double minRating) {
        return tradingSystem.searchCategoryInStore(userName, token, category, store_name, minPrice, maxPrice, minRating);
    }

    @GetMapping("/store/search/keywords")
    public ResponseEntity<String> searchKeywordsInStore(@RequestParam String userName, @RequestParam String token, @RequestParam String keyWords,
                                                        @RequestParam String store_name, @RequestParam Double minPrice, @RequestParam Double maxPrice,
                                                        @RequestParam Double minRating, @RequestParam int category) {
        return tradingSystem.searchKeywordsInStore(userName, token, keyWords, store_name, minPrice, maxPrice, minRating, category);
    }

    @GetMapping("/stores/search/name")
    public ResponseEntity<String> searchNameInStores(@RequestParam String userName, @RequestParam String token, @RequestParam String productName,
                                                     @RequestParam Double minPrice, @RequestParam Double maxPrice, @RequestParam Double minRating,
                                                     @RequestParam int category, @RequestParam Double storeRating) {
        return tradingSystem.searchNameInStores(userName, token, productName, minPrice, maxPrice, minRating, category, storeRating);
    }

    @GetMapping("/stores/search/category")
    public ResponseEntity<String> searchCategoryInStores(@RequestParam String userName, @RequestParam String token, @RequestParam int category,
                                                         @RequestParam Double minPrice, @RequestParam Double maxPrice, @RequestParam Double minRating,
                                                         @RequestParam Double storeRating) {
        return tradingSystem.searchCategoryInStores(userName, token, category, minPrice, maxPrice, minRating, storeRating);
    }

    @GetMapping("/stores/search/keywords")
    public ResponseEntity<String> searchKeywordsInStores(@RequestParam String userName, @RequestParam String token, @RequestParam String keyWords,
                                                         @RequestParam Double minPrice, @RequestParam Double maxPrice, @RequestParam Double minRating,
                                                         @RequestParam int category, @RequestParam Double storeRating) {
        return tradingSystem.searchKeywordsInStores(userName, token, keyWords, minPrice, maxPrice, minRating, category, storeRating);
    }

    @PostMapping("/purchase/approve")
    public ResponseEntity<String> approvePurchase(@RequestParam String username, @RequestParam String token) {
        return tradingSystem.approvePurchase(username, token);
    }

    @GetMapping("/purchase/history")
    public ResponseEntity<String> getPurchaseHistory(@RequestParam String username, @RequestParam String token, @RequestParam String storeName) {
        return tradingSystem.getPurchaseHistory(username, token, storeName);
    }

    @GetMapping("/stores/purchase/history")
    public ResponseEntity<String> getStoresPurchaseHistory(@RequestParam String username, @RequestParam String token, @RequestParam String storeName, @RequestParam Integer productBarcode) {
        return tradingSystem.getStoresPurchaseHistory(username, token, storeName, productBarcode);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(@RequestParam String username, @RequestParam String token, @RequestParam int productId, @RequestParam String storeName, @RequestParam int quantity) {
        return tradingSystem.addToCart(username, token, productId, storeName, quantity);
    }

    @PostMapping("/cart/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam String username, @RequestParam String token, @RequestParam int productId, @RequestParam String storeName, @RequestParam int quantity) {
        return tradingSystem.removeFromCart(username, token, productId, storeName, quantity);
    }

    @GetMapping("/cart/view")
    public ResponseEntity<String> viewCart(@RequestParam String username, @RequestParam String token) {
        return tradingSystem.viewCart(username, token);
    }

    @GetMapping("/purchase/history/all")
    public ResponseEntity<String> getAllHistoryPurchases(@RequestParam String userName, @RequestParam String token, @RequestParam String storeName) {
        return tradingSystem.getAllHistoryPurchases(userName, token, storeName);
    }

    @GetMapping("/purchase/history/customer")
    public ResponseEntity<String> getHistoryPurchasesByCustomer(@RequestParam String userName, @RequestParam String token, @RequestParam String storeName, @RequestParam String customerUserName) {
        return tradingSystem.getHistoryPurchasesByCustomer(userName, token, storeName, customerUserName);
    }

    @GetMapping("/store/officials/info")
    public ResponseEntity<String> requestInformationAboutOfficialsInStore(@RequestParam String userName,
                                                                          @RequestParam String token,
                                                                          @RequestParam String storeName) {
        return tradingSystem.requestInformationAboutOfficialsInStore(userName, token, storeName);
    }

    @GetMapping("/store/manager/permissions")
    public ResponseEntity<String> requestManagersPermissions(@RequestParam String userName,
                                                             @RequestParam String token,
                                                             @RequestParam String storeName) {
        return tradingSystem.requestManagersPermissions(userName, token, storeName);
    }

    @GetMapping("/store/official/info")
    public ResponseEntity<String> requestInformationAboutSpecificOfficialInStore(@RequestParam String userName,
                                                                                 @RequestParam String token,
                                                                                 @RequestParam String storeName,
                                                                                 @RequestParam String officialUserName) {
        return tradingSystem.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, officialUserName);
    }

    @GetMapping("/cart/price/calculate")
    public ResponseEntity<String> calculatePrice(@RequestParam String username, @RequestParam String token) {
        return tradingSystem.calculatePrice(username, token);
    }
    //region Discount creation
    // Discount Policies
    @GetMapping("/store/{storeName}/discount-policies")
    public ResponseEntity<String> getDiscountPolicies(@RequestParam String username,
                                                      @RequestParam String token,
                                                      @PathVariable String storeName) {
        return tradingSystem.getDiscountPolicies(username, token, storeName);
    }

    @GetMapping("/store/{storeName}/discount-conditions")
    public ResponseEntity<String> getDiscountConditions(@RequestParam String username,
                                                        @RequestParam String token,
                                                        @PathVariable String storeName) {
        return tradingSystem.getDiscountConditions(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/discounts/category-percentage")
    public ResponseEntity<String> addCategoryPercentageDiscount(@RequestParam String username,
                                                                @RequestParam String token,
                                                                @PathVariable String storeName,
                                                                @RequestParam int category,
                                                                @RequestParam double discountPercent) {
        return tradingSystem.addCategoryPercentageDiscount(username, token, storeName, category, discountPercent);
    }

    @PostMapping("/store/{storeName}/discounts/product-percentage")
    public ResponseEntity<String> addProductPercentageDiscount(@RequestParam String username,
                                                               @RequestParam String token,
                                                               @PathVariable String storeName,
                                                               @RequestParam int productId,
                                                               @RequestParam double discountPercent) {
        return tradingSystem.addProductPercentageDiscount(username, token, storeName, productId, discountPercent);
    }

    @PostMapping("/store/{storeName}/discounts/store")
    public ResponseEntity<String> addStoreDiscount(@RequestParam String username,
                                                   @RequestParam String token,
                                                   @PathVariable String storeName,
                                                   @RequestParam double discountPercent) {
        return tradingSystem.addStoreDiscount(username, token, storeName, discountPercent);
    }

    @PostMapping("/store/{storeName}/discounts/conditional")
    public ResponseEntity<String> addConditionalDiscount(@RequestParam String username,
                                                         @RequestParam String token,
                                                         @PathVariable String storeName) {
        return tradingSystem.addConditionalDiscount(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/discounts/additive")
    public ResponseEntity<String> addAdditiveDiscount(@RequestParam String username,
                                                      @RequestParam String token,
                                                      @PathVariable String storeName) {
        return tradingSystem.addAdditiveDiscount(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/discounts/max")
    public ResponseEntity<String> addMaxDiscount(@RequestParam String username,
                                                 @RequestParam String token,
                                                 @PathVariable String storeName) {
        return tradingSystem.addMaxDiscount(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/conditions/category-count")
    public ResponseEntity<String> addCategoryCountCondition(@RequestParam String username,
                                                            @RequestParam String token,
                                                            @PathVariable String storeName,
                                                            @RequestParam int category,
                                                            @RequestParam int count) {
        return tradingSystem.addCategoryCountCondition(username, token, storeName, category, count);
    }

    @PostMapping("/store/{storeName}/conditions/total-sum")
    public ResponseEntity<String> addTotalSumCondition(@RequestParam String username,
                                                       @RequestParam String token,
                                                       @PathVariable String storeName,
                                                       @RequestParam double requiredSum) {
        return tradingSystem.addTotalSumCondition(username, token, storeName, requiredSum);
    }

    @PostMapping("/store/{storeName}/conditions/product-count")
    public ResponseEntity<String> addProductCountCondition(@RequestParam String username,
                                                           @RequestParam String token,
                                                           @PathVariable String storeName,
                                                           @RequestParam int productId,
                                                           @RequestParam int count) {
        return tradingSystem.addProductCountCondition(username, token, storeName, productId, count);
    }

    @PostMapping("/store/{storeName}/discounts/and")
    public ResponseEntity<String> addAndDiscount(@RequestParam String username,
                                                 @RequestParam String token,
                                                 @PathVariable String storeName) {
        return tradingSystem.addAndDiscount(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/discounts/or")
    public ResponseEntity<String> addOrDiscount(@RequestParam String username,
                                                @RequestParam String token,
                                                @PathVariable String storeName) {
        return tradingSystem.addOrDiscount(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/discounts/xor")
    public ResponseEntity<String> addXorDiscount(@RequestParam String username,
                                                 @RequestParam String token,
                                                 @PathVariable String storeName) {
        return tradingSystem.addXorDiscount(username, token, storeName);
    }

    @DeleteMapping("/store/{storeName}/discounts/{selectedIndex}")
    public ResponseEntity<String> removeDiscount(@RequestParam String username,
                                                 @RequestParam String token,
                                                 @PathVariable String storeName,
                                                 @PathVariable int selectedIndex) {
        return tradingSystem.removeDiscount(username, token, storeName, selectedIndex);
    }
    //endregion

    // Purchase Policies
    @GetMapping("/store/{storeName}/purchase-policies")
    public ResponseEntity<String> getPurchasePoliciesInfo(@RequestParam String username,
                                                          @RequestParam String token,
                                                          @PathVariable String storeName) {
        return tradingSystem.getPurchasePoliciesInfo(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/purchase-policies/age")
    public ResponseEntity<String> addPurchasePolicyByAge(@RequestParam String username,
                                                         @RequestParam String token,
                                                         @PathVariable String storeName,
                                                         @RequestParam int ageToCheck,
                                                         @RequestParam int category) {
        return tradingSystem.addPurchasePolicyByAge(username, token, storeName, ageToCheck, category);
    }


    @PostMapping("/store/{storeName}/purchase-policies/category-date")
    public ResponseEntity<String> addPurchasePolicyByCategoryAndDate(@RequestParam String username,
                                                                     @RequestParam String token,
                                                                     @PathVariable String storeName,
                                                                     @RequestParam int category,
                                                                     @RequestParam LocalDateTime dateTime) {
        return tradingSystem.addPurchasePolicyByCategoryAndDate(username, token, storeName, category, dateTime);
    }

    @PostMapping("/store/{storeName}/purchase-policies/date")
    public ResponseEntity<String> addPurchasePolicyByDate(@RequestParam String username,
                                                          @RequestParam String token,
                                                          @PathVariable String storeName,
                                                          @RequestParam LocalDateTime dateTime) {
        return tradingSystem.addPurchasePolicyByDate(username, token, storeName, dateTime);
    }

    @PostMapping("/store/{storeName}/purchase-policies/product-date")
    public ResponseEntity<String> addPurchasePolicyByProductAndDate(@RequestParam String username,
                                                                    @RequestParam String token,
                                                                    @PathVariable String storeName,
                                                                    @RequestParam int productId,
                                                                    @RequestParam LocalDateTime dateTime) {
        return tradingSystem.addPurchasePolicyByProductAndDate(username, token, storeName, productId, dateTime);
    }

    @PostMapping("/store/{storeName}/purchase-policies/cart-max-products")
    public ResponseEntity<String> addPurchasePolicyByShoppingCartMaxProductsUnit(@RequestParam String username,
                                                                                 @RequestParam String token,
                                                                                 @PathVariable String storeName,
                                                                                 @RequestParam int productId,
                                                                                 @RequestParam int numOfQuantity) {
        return tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, productId, numOfQuantity);
    }

    @PostMapping("/store/{storeName}/purchase-policies/cart-min-products")
    public ResponseEntity<String> addPurchasePolicyByShoppingCartMinProducts(@RequestParam String username,
                                                                             @RequestParam String token,
                                                                             @PathVariable String storeName,
                                                                             @RequestParam int numOfQuantity) {
        return tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, numOfQuantity);
    }

    @PostMapping("/store/{storeName}/purchase-policies/cart-min-products-unit")
    public ResponseEntity<String> addPurchasePolicyByShoppingCartMinProductsUnit(@RequestParam String username,
                                                                                 @RequestParam String token,
                                                                                 @PathVariable String storeName,
                                                                                 @RequestParam int productId,
                                                                                 @RequestParam int numOfQuantity) {
        return tradingSystem.addPurchasePolicyByShoppingCartMinProductsUnit(username, token, storeName, productId, numOfQuantity);
    }

    @PostMapping("/store/{storeName}/purchase-policies/and")
    public ResponseEntity<String> addAndPurchasePolicy(@RequestParam String username,
                                                       @RequestParam String token,
                                                       @PathVariable String storeName) {
        return tradingSystem.addAndPurchasePolicy(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/purchase-policies/or")
    public ResponseEntity<String> addOrPurchasePolicy(@RequestParam String username,
                                                      @RequestParam String token,
                                                      @PathVariable String storeName) {
        return tradingSystem.addOrPurchasePolicy(username, token, storeName);
    }

    @PostMapping("/store/{storeName}/purchase-policies/conditioning")
    public ResponseEntity<String> addConditioningPurchasePolicy(@RequestParam String username,
                                                                @RequestParam String token,
                                                                @PathVariable String storeName) {
        return tradingSystem.addConditioningPurchasePolicy(username, token, storeName);
    }

    @PutMapping("/store/{storeName}/purchase-policies/{selectedIndex}/product-id")
    public ResponseEntity<String> setPurchasePolicyProductId(@RequestParam String username,
                                                             @RequestParam String token,
                                                             @PathVariable String storeName,
                                                             @PathVariable int selectedIndex,
                                                             @RequestParam int productId) {
        return tradingSystem.setPurchasePolicyProductId(username, token, storeName, selectedIndex, productId);
    }

    @PutMapping("/store/{storeName}/purchase-policies/{selectedIndex}/quantity")
    public ResponseEntity<String> setPurchasePolicyNumOfQuantity(@RequestParam String username,
                                                                 @RequestParam String token,
                                                                 @PathVariable String storeName,
                                                                 @PathVariable int selectedIndex,
                                                                 @RequestParam int numOfQuantity) {
        return tradingSystem.setPurchasePolicyNumOfQuantity(username, token, storeName, selectedIndex, numOfQuantity);
    }

    @PutMapping("/store/{storeName}/purchase-policies/{selectedIndex}/date")
    public ResponseEntity<String> setPurchasePolicyDateTime(@RequestParam String username,
                                                            @RequestParam String token,
                                                            @PathVariable String storeName,
                                                            @PathVariable int selectedIndex,
                                                            @RequestParam LocalDateTime dateTime) {
        return tradingSystem.setPurchasePolicyDateTime(username, token, storeName, selectedIndex, dateTime);
    }

    @PutMapping("/store/{storeName}/purchase-policies/{selectedIndex}/age")
    public ResponseEntity<String> setPurchasePolicyAge(@RequestParam String username,
                                                       @RequestParam String token,
                                                       @PathVariable String storeName,
                                                       @PathVariable int selectedIndex,
                                                       @RequestParam int age) {
        return tradingSystem.setPurchasePolicyAge(username, token, storeName, selectedIndex, age);
    }

    @PutMapping("/store/{storeName}/purchase-policies/{selectedDiscountIndex}/first")
    public ResponseEntity<String> setFirstPurchasePolicy(@RequestParam String username,
                                                         @RequestParam String token,
                                                         @PathVariable String storeName,
                                                         @PathVariable int selectedDiscountIndex,
                                                         @RequestParam int selectedFirstIndex) {
        return tradingSystem.setFirstPurchasePolicy(username, token, storeName, selectedDiscountIndex, selectedFirstIndex);
    }

    @PutMapping("/store/{storeName}/purchase-policies/{selectedDiscountIndex}/second")
    public ResponseEntity<String> setSecondPurchasePolicy(@RequestParam String username,
                                                          @RequestParam String token,
                                                          @PathVariable String storeName,
                                                          @PathVariable int selectedDiscountIndex,
                                                          @RequestParam int selectedSecondIndex) {
        return tradingSystem.setSecondPurchasePolicy(username, token, storeName, selectedDiscountIndex, selectedSecondIndex);
    }

    @DeleteMapping("/store/{storeName}/purchase-policies/{selectedIndex}")
    public ResponseEntity<String> removePurchasePolicy(@RequestParam String username,
                                                       @RequestParam String token,
                                                       @PathVariable String storeName,
                                                       @PathVariable int selectedIndex) {
        return tradingSystem.removePurchasePolicy(username, token, storeName, selectedIndex);
    }
}
