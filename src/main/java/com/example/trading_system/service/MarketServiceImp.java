package com.example.trading_system.service;

import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StoreRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarketServiceImp implements MarketService {
    private static MarketServiceImp instance = null;

    public MarketFacade getMarketFacade() {
        return marketFacade;
    }

    private MarketFacade marketFacade;

    private MarketServiceImp(StoreRepository storeRepository) {
        marketFacade = MarketFacadeImp.getInstance(storeRepository);
    }

    public static MarketServiceImp getInstance(StoreRepository storeRepository) {
        if (instance == null) instance = new MarketServiceImp(storeRepository);
        return instance;
    }

    @Override
    public void deleteInstance() {
        instance = null;
        this.marketFacade.deleteInstance();
        this.marketFacade = null;
    }

    @Override
    public String getAllStores(String userName) {
        return marketFacade.getAllStores(userName);
    }

    @Override
    public String getAllStoresInJSONFormat(String username) {
        return marketFacade.getAllStoresInJSONFormat(username);
    }

    @Override
    public String getProductsFromStoreJSONFormat(String storeName) {
        return marketFacade.getProductsFromStoreJSONFormat(storeName);
    }

    @Override
    public String getStoresIOpened(String username) {
        return marketFacade.getStoresIOpened(username);
    }

    @Override
    public String getCategories(String username) {
        return marketFacade.getCategories(username);
    }

    @Override
    public void openStoreExist(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        marketFacade.openStoreExist(userName, storeName);
    }

    @Override
    public String getStoreProducts(String userName, String store_name) throws IllegalAccessException {
        return marketFacade.getStoreProducts(userName, store_name);
    }

    @Override
    public String getProductInfo(String userName, String store_name, int product_Id) throws IllegalAccessException {
        return marketFacade.getProductInfo(userName, store_name, product_Id);
    }

    @Override
    public String searchProductsInStores(String userName, String keyWord, double minPrice, double maxPrice, List<Integer> intCategories, Double rating) throws Exception {
        return marketFacade.searchProductsInStores(userName, keyWord, minPrice, maxPrice, intCategories, rating);
    }

    @Override
    public String searchNameInStore(String userName, String productName, String store_name, Double minPrice, Double maxPrice, Double minRating, int category) throws IllegalAccessException {
        return marketFacade.searchNameInStore(userName, productName, store_name, minPrice, maxPrice, minRating, category);
    }

    @Override
    public String searchCategoryInStore(String userName, int category, String store_name, Double minPrice, Double maxPrice, Double minRating) throws IllegalAccessException {
        return marketFacade.searchCategoryInStore(userName, category, store_name, minPrice, maxPrice, minRating);
    }

    @Override
    public String searchKeywordsInStore(String userName, String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, int category) throws IllegalAccessException {
        return marketFacade.searchKeywordsInStore(userName, keyWords, store_name, minPrice, maxPrice, minRating, category);
    }

    @Override
    public String searchNameInStores(String userName, String productName, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        return marketFacade.searchNameInStores(userName, productName, minPrice, maxPrice, minRating, category, storeRating);
    }

    @Override
    public String searchCategoryInStores(String userName, int category, Double minPrice, Double maxPrice, Double minRating, Double storeRating) {
        return marketFacade.searchCategoryInStores(userName, category, minPrice, maxPrice, minRating, storeRating);
    }

    @Override
    public String searchKeywordsInStores(String userName, String keyWords, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        return marketFacade.searchKeywordsInStores(userName, keyWords, minPrice, maxPrice, minRating, category, storeRating);
    }


    @Override
    public void addProduct(String username, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        marketFacade.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
    }

    @Override
    public void removeProduct(String username, String store_name, int product_id) throws IllegalAccessException {
        marketFacade.removeProduct(username, store_name, product_id);
    }

    @Override
    public void setProductName(String username, String store_name, int productId, String product_name) throws IllegalAccessException {
        marketFacade.setProductName(username, store_name, productId, product_name);
    }

    @Override
    public void editProduct(String username, String storeName, int productId, String productName, String productDescription, double productPrice, int productQuantity) throws Exception {
        marketFacade.editProduct(username, storeName, productId, productName, productDescription, productPrice, productQuantity);
    }
    @Override
    public void setProductDescription(String username, String store_name, int productId, String product_description) throws IllegalAccessException {
        marketFacade.setProductDescription(username, store_name, productId, product_description);
    }

    @Override
    public void setProductPrice(String username, String store_name, int productId, double product_price) throws IllegalAccessException {
        marketFacade.setProductPrice(username, store_name, productId, product_price);
    }

    @Override
    public void setProductQuantity(String username, String store_name, int productId, int product_quantity) throws IllegalAccessException {
        marketFacade.setProductQuantity(username, store_name, productId, product_quantity);
    }

    @Override
    public void setRating(String username, String store_name, int productId, double rating) throws IllegalAccessException {
        marketFacade.setRating(username, store_name, productId, rating);
    }

    @Override
    public void setCategory(String username, String store_name, int productId, int category) throws IllegalAccessException {
        marketFacade.setCategory(username, store_name, productId, category);
    }

    @Override
    public void addKeywordToProduct(String username, String storeName, int productId, String keyword) throws IllegalAccessException {
        marketFacade.addKeywordToProduct(username,storeName,productId,keyword);
    }

    @Override
    public void removeKeywordToProduct(String username, String storeName, int productId, String keyword) throws IllegalAccessException {
       marketFacade.removeKeywordToProduct(username,storeName,productId,keyword);
    }

    @Override
    public String getDiscountPolicies(String username, String storeName) throws IllegalAccessException {
        return marketFacade.getDiscountPolicies(username, storeName);
    }

    @Override
    public String getDiscountConditions(String username, String storeName) throws IllegalAccessException {
        return marketFacade.getDiscountConditions(username, storeName);
    }

    @Override
    public void addCategoryPercentageDiscount(String username, String storeName, int category, double discountPercent) throws IllegalAccessException {
        marketFacade.addCategoryPercentageDiscount(username, storeName, category, discountPercent);
    }

    @Override
    public void addProductPercentageDiscount(String username, String storeName, int productId, double discountPercent) throws IllegalAccessException {
        marketFacade.addProductPercentageDiscount(username, storeName, productId, discountPercent);
    }

    @Override
    public void addStoreDiscount(String username, String storeName, double discountPercent) throws IllegalAccessException {
        marketFacade.addStoreDiscount(username, storeName, discountPercent);
    }

    @Override
    public void addConditionalDiscount(String username, String storeName) throws IllegalAccessException {
        marketFacade.addConditionalDiscount(username, storeName);
    }

    @Override
    public void addAdditiveDiscount(String username, String storeName) throws IllegalAccessException {
        marketFacade.addAdditiveDiscount(username, storeName);
    }

    @Override
    public void addMaxDiscount(String username, String storeName) throws IllegalAccessException {
        marketFacade.addMaxDiscount(username, storeName);
    }

    @Override
    public void addCategoryCountCondition(String username, String storeName, int category, int count) throws IllegalAccessException {
        marketFacade.addCategoryCountCondition(username, storeName, category, count);
    }

    @Override
    public void addTotalSumCondition(String username, String storeName, double requiredSum) throws IllegalAccessException {
        marketFacade.addTotalSumCondition(username, storeName, requiredSum);
    }

    @Override
    public void addProductCountCondition(String username, String storeName, int productId, int count) throws IllegalAccessException {
        marketFacade.addProductCountCondition(username, storeName, productId, count);
    }

    @Override
    public void addAndDiscount(String username, String storeName) throws IllegalAccessException {
        marketFacade.addAndDiscount(username, storeName);
    }

    @Override
    public void addOrDiscount(String username, String storeName) throws IllegalAccessException {
        marketFacade.addOrDiscount(username, storeName);
    }

    @Override
    public void addXorDiscount(String username, String storeName) throws IllegalAccessException {
        marketFacade.addXorDiscount(username, storeName);
    }

    @Override
    public void removeDiscount(String username, String storeName, int selectedIndex) throws IllegalAccessException {
        marketFacade.removeDiscount(username, storeName, selectedIndex);
    }

    @Override
    public void setFirstDiscount(String username, String storeName, int selectedDiscountIndex, int selectedFirstIndex) throws IllegalAccessException {
        marketFacade.setFirstDiscount(username, storeName, selectedDiscountIndex, selectedFirstIndex);
    }

    @Override
    public void setSecondDiscount(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        marketFacade.setSecondDiscount(username, storeName, selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setFirstCondition(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        marketFacade.setFirstCondition(username, storeName, selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setSecondCondition(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        marketFacade.setSecondCondition(username, storeName, selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setThenDiscount(String username, String storeName, int selectedDiscountIndex, int selectedThenIndex) throws IllegalAccessException {
        marketFacade.setThenDiscount(username, storeName, selectedDiscountIndex, selectedThenIndex);
    }

    @Override
    public void setCategoryDiscount(String username, String storeName, int selectedDiscountIndex, int category) throws IllegalAccessException {
        marketFacade.setCategoryDiscount(username, storeName, selectedDiscountIndex, category);
    }

    @Override
    public void setProductIdDiscount(String username, String storeName, int selectedDiscountIndex, int productId) throws IllegalAccessException {
        marketFacade.setProductIdDiscount(username, storeName, selectedDiscountIndex, productId);
    }

    @Override
    public void setPercentDiscount(String username, String storeName, int selectedDiscountIndex, double discountPercent) throws IllegalAccessException {
        marketFacade.setPercentDiscount(username, storeName, selectedDiscountIndex, discountPercent);
    }

    @Override
    public void setDeciderDiscount(String username, String storeName, int selectedDiscountIndex, int selectedDeciderIndex) throws IllegalAccessException {
        marketFacade.setDeciderDiscount(username, storeName, selectedDiscountIndex, selectedDeciderIndex);
    }

    @Override
    public void setTotalSum(String username, String storeName, int selectedConditionIndex, double newSum) throws IllegalAccessException {
        marketFacade.setTotalSum(username, storeName, selectedConditionIndex, newSum);
    }

    @Override
    public void setCountCondition(String username, String storeName, int selectedConditionIndex, int newCount) throws IllegalAccessException {
        marketFacade.setCountCondition(username, storeName, selectedConditionIndex, newCount);
    }

    @Override
    public void setCategoryCondition(String username, String storeName, int selectedConditionIndex, int newCategory) throws IllegalAccessException {
        marketFacade.setCategoryCondition(username, storeName, selectedConditionIndex, newCategory);
    }

    @Override
    public void removeCondition(String username, String storeName, int selectedIndex) throws IllegalAccessException {
        marketFacade.removeCondition(username, storeName, selectedIndex);
    }

    @Override
    public String getPurchasePoliciesInfo(String username, String storeName) throws IllegalAccessException {
        return marketFacade.getPurchasePoliciesInfo(username, storeName);
    }

    @Override
    public void addPurchasePolicyByAge(String username, String storeName, int ageToCheck, int category) throws IllegalAccessException {
        marketFacade.addPurchasePolicyByAge(username, storeName, ageToCheck, category);
    }

    @Override
    public void addPurchasePolicyByCategoryAndDate(String username, String storeName, int category, LocalDateTime dateTime) throws IllegalAccessException {
        marketFacade.addPurchasePolicyByCategoryAndDate(username, storeName, category, dateTime);
    }

    @Override
    public void addPurchasePolicyByDate(String username, String storeName, LocalDateTime dateTime) throws IllegalAccessException {
        marketFacade.addPurchasePolicyByDate(username, storeName, dateTime);
    }

    @Override
    public void addPurchasePolicyByProductAndDate(String username, String storeName, int productId, LocalDateTime dateTime) throws IllegalAccessException {
        marketFacade.addPurchasePolicyByProductAndDate(username, storeName, productId, dateTime);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMaxProductsUnit(String username, String storeName, int productId, int numOfQuantity) throws IllegalAccessException {
        marketFacade.addPurchasePolicyByShoppingCartMaxProductsUnit(username, storeName, productId, numOfQuantity);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMinProducts(String username, String storeName, int numOfQuantity) throws IllegalAccessException {
        marketFacade.addPurchasePolicyByShoppingCartMinProducts(username, storeName, numOfQuantity);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMinProductsUnit(String username, String storeName, int productId, int numOfQuantity) throws IllegalAccessException {
        marketFacade.addPurchasePolicyByShoppingCartMinProductsUnit(username, storeName, productId, numOfQuantity);
    }

    @Override
    public void addAndPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        marketFacade.addAndPurchasePolicy(username, storeName);
    }

    @Override
    public void addOrPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        marketFacade.addOrPurchasePolicy(username, storeName);
    }

    @Override
    public void addConditioningPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        marketFacade.addConditioningPurchasePolicy(username, storeName);
    }

    @Override
    public void setPurchasePolicyProductId(String username, String storeName, int selectedIndex, int productId) throws IllegalAccessException {
        marketFacade.setPurchasePolicyProductId(username, storeName, selectedIndex, productId);
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(String username, String storeName, int selectedIndex, int numOfQuantity) throws IllegalAccessException {
        marketFacade.setPurchasePolicyNumOfQuantity(username, storeName, selectedIndex, numOfQuantity);
    }

    @Override
    public void setPurchasePolicyDateTime(String username, String storeName, int selectedIndex, LocalDateTime dateTime) throws IllegalAccessException {
        marketFacade.setPurchasePolicyDateTime(username, storeName, selectedIndex, dateTime);
    }

    @Override
    public void setPurchasePolicyAge(String username, String storeName, int selectedIndex, int age) throws IllegalAccessException {
        marketFacade.setPurchasePolicyAge(username, storeName, selectedIndex, age);
    }

    @Override
    public void setFirstPurchasePolicy(String username, String storeName, int selectedDiscountIndex, int selectedFirstIndex) throws IllegalAccessException {
        marketFacade.setFirstPurchasePolicy(username, storeName, selectedDiscountIndex, selectedFirstIndex);
    }

    @Override
    public void setSecondPurchasePolicy(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        marketFacade.setSecondPurchasePolicy(username, storeName, selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void removePurchasePolicy(String username, String storeName, int selectedIndex) throws IllegalAccessException {
        marketFacade.removePurchasePolicy(username, storeName, selectedIndex);
    }


    @Override
    public void closeStoreExist(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        marketFacade.closeStoreExist(userName, storeName);
    }

    @Override
    public String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException {
        return marketFacade.getAllHistoryPurchases(userName, storeName);
    }

    @Override
    public String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException {
        return marketFacade.getHistoryPurchasesByCustomer(userName, storeName, customerUserName);
    }

    @Override
    public String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException,JsonProcessingException {
        return marketFacade.requestInformationAboutOfficialsInStore(userName, storeName);
    }


    public String getPurchaseHistoryJSONFormatForStore(String userName,String storeName) {
        return marketFacade.getPurchaseHistoryJSONFormatForStore(userName,storeName);
    }

    @Override
    public String getPurchaseHistoryJSONFormat(String userName) throws IllegalAccessException {
        return marketFacade.getPurchaseHistoryJSONFormat(userName);
    }

    @Override
    public String requestManagersPermissions(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        return marketFacade.requestManagersPermissions(userName, storeName);
    }

    @Override
    public String requestInformationAboutSpecificOfficialInStore(String userName, String storeName, String officialUserName) throws IllegalArgumentException, IllegalAccessException {
        return marketFacade.requestInformationAboutSpecificOfficialInStore(userName, storeName, officialUserName);
    }

    @Override
    public void sendMessageUserToStore(String sender, String storeName, String content){
        marketFacade.sendMessageUserToStore(sender, storeName, content);
    }

    @Override
    public void sendMessageStoreToUser(String owner, String receiver, String storeName, String content){
        marketFacade.sendMessageStoreToUser(owner, receiver, storeName, content);
    }

    @Override
    public String getStoreMessagesJson(String admin, String storename){
        return marketFacade.getStoreMessagesJson(admin, storename);
    }

    @Override
    public void placeBid(String userName, String storeName, int productID, double price) throws  IllegalArgumentException {
        marketFacade.placeBid(userName, storeName, productID, price);
    }

    @Override
    public void approveBid(String userName, String storeName, int productID, String bidUserName) throws Exception {
        marketFacade.approveBid(userName, storeName, productID, bidUserName);
    }

    @Override
    public void rejectBid(String userName, String storeName, int productID, String bidUserName) throws IllegalArgumentException, IllegalAccessException{
        marketFacade.rejectBid(userName, storeName, productID, bidUserName);
    }

    @Override
    public void placeCounterOffer(String userName, String storeName, int productID, String bidUserName, double newPrice) throws IllegalAccessException, IllegalArgumentException{
        marketFacade.placeCounterOffer(userName, storeName, productID, bidUserName, newPrice);
    }

    @Override
    public String getStoreBids(String userName, String storeName) throws IllegalAccessException, IllegalArgumentException{
        return marketFacade.getStoreBids(userName, storeName);
    }

    @Override
    public String getMyBids(String userName, String storeName) throws IllegalAccessException, IllegalArgumentException{
        return marketFacade.getMyBids(userName, storeName);
    }

//    @Override
//    public String buyLotteryProductTicket(String userName, String storeName, int productID, double price) throws Exception{
//        return marketFacade.buyLotteryProductTicket(userName, storeName, productID, price);
//    }
//
//    @Override
//    public void createProductLottery(String userName, String storeName, int productID, LocalDateTime localDateTime, double price) throws Exception{
//        marketFacade.createProductLottery(userName, storeName, productID, localDateTime, price);
//    }
//
//
//


}




