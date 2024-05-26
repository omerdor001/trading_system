package com.example.trading_system.service;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MarketServiceImp implements MarketService {
    private final MarketFacade marketFacade;
    private static final Logger logger = LoggerFactory.getLogger(MarketServiceImp.class);

    public MarketServiceImp(MarketFacade marketFacade) {
        this.marketFacade = marketFacade;
    }

    @Override
    public String getAllStores() {
        String result;
        logger.info("Trying to Gather All Stores");
        try {
            result = marketFacade.getAllStores();
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Stores Info ", e.getMessage());
            return "";
        }
        logger.info("FINISHED Gather All Stores Info");
        return result;
    }
    @Override
    public void openStoreExist(String storeName) {
        logger.info("Trying to open store with name : {}", storeName);
        try {
            marketFacade.openStoreExist(storeName);
        }
        catch (Exception e) {
            logger.error("Error occurred : {} , Failed on opening Store with name : {}", e.getMessage(), storeName);
        }
    }

    @Override
    public void closeStoreExist(String storeName) {
        logger.info("Trying to close store with name : {}", storeName);
        try {
            marketFacade.openStoreExist(storeName);

        }
        catch (Exception e) {
            logger.error("Error occurred : {} , Failed on closing Store with name : {}", e.getMessage(), storeName);
        }
    }

    @Override
    public String getStoreProducts(String storeName) {
        String result;
        logger.info("Trying to Gather ALL Store Products");
        try {
            result = marketFacade.getStoreProducts(storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Store Products Info with Store Id : {} ", e.getMessage(), storeName);
            return "";
        }
        logger.info("FINISHED Gather ALL Store Products Info");
        return result;
    }

    @Override
    public String getProductInfo(String storeName, int productId) {
        String result;
        logger.info("Trying to Gather Product Info with Store Id : {} and product ID: {}", storeName, productId);
        try {
            result = marketFacade.getProductInfo(storeName, productId);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Product Info with Store Id : {} and product ID:{}", e.getMessage(), storeName, productId);
            return "";
        }
        logger.info("FINISHED Gather Product Info");
        return result;
    }

    //search in specific store
    @Override
    public String searchNameInStore(String name, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category) {
        String result;
        logger.info("Trying to search products in store : {} with name : {}", storeName, name);
        try {
            result = marketFacade.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with name : {}}", e.getMessage(), storeName, name);
            return "";
        }
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    @Override
    public String searchCategoryInStore(Category category, String storeName, Double minPrice, Double maxPrice, Double minRating) {
        String result;
        logger.info("Trying to search products in store : {} with category, : {}", storeName, category);
        try {
            result = marketFacade.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with category : {}}", e.getMessage(), storeName, category);
            return "";
        }
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    @Override
    public String searchKeywordsInStore(String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category) {
        String result;
        logger.info("Trying to search products in store : {} with keyWords,  : {}", storeName, keyWords);
        try {
            result = marketFacade.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with keyWords,  : {}}", e.getMessage(), storeName, keyWords);
            return "";
        }
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    //search in stores
    @Override
    public String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        String result;
        logger.info("Trying to search products in stores with name : {}", name);
        try {
            result = marketFacade.searchNameInStores(name, minPrice, maxPrice, minRating, category);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in stores: {}}", e.getMessage(), name);
            return "";
        }
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    @Override
    public String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating) {
        String result;
        logger.info("Trying to search products in stores with category, : {}", category);
        try {
            result = marketFacade.searchCategoryInStores(category, minPrice, maxPrice, minRating);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in stores with category : {}}", e.getMessage(), category);
            return "";
        }
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    @Override
    public String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category) {
        String result;
        logger.info("Trying to search products in stores with keyWords,  : {}", keyWords);
        try {
            result = marketFacade.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in stores with keyWords,  : {}}", e.getMessage(), keyWords);
            return "";
        }
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    @Override
    public ResponseEntity<String> addProduct(String username, int productId, String storeName, String productName, String productDescription,
                                             double productPrice, int productQuantity, double rating, int category, List<String> keyWords){
        boolean result;
        logger.info("Trying to add products to store : {}", storeName);
        try {
            result = marketFacade.addProduct(username, productId, storeName, productName, productDescription, productPrice, productQuantity,rating,category,keyWords);
            if(result){
                return new ResponseEntity<>("Success adding products", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  while trying to add products to store : {}", e.getMessage(), storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished add products to store : {}", storeName);
        return new ResponseEntity<>("Success adding products", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> removeProduct(String username, String storeName, int productId){
        boolean result;
        logger.info("Trying to remove products to store : {}", storeName);
        try {
            result = marketFacade.removeProduct(username, storeName, productId);
            if(result){
                return new ResponseEntity<>("Success removing products", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to remove products to store : {}", e.getMessage(), storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished remove products to store : {}", storeName);
        return new ResponseEntity<>("Success removing products", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductName(String username, String storeNameId, int productId, String productName){
        boolean result;
        logger.info("Trying to edit name to product : {}", productId);
        try {
            result = marketFacade.setProductName(username, storeNameId,productId, productName);
            if(result){
                return new ResponseEntity<>("Success editing name to product", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to edit name to product : {}", e.getMessage(),productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit name of product : {}",productId);
        return new ResponseEntity<>("Success editing name to product", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductDescription(String username, String storeNameId, int productId, String productDescription){
        boolean result;
        logger.info("Trying to edit description to product : {}", productId);
        try {
            result = marketFacade.setProductDescription(username, storeNameId,productId, productDescription);
            if(result){
                return new ResponseEntity<>("Success editing name to product", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to edit description to product : {}", e.getMessage(),productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit description of product : {}",productId);
        return new ResponseEntity<>("Success editing description to product", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductPrice(String username, String storeNameId, int productId, int productPrice){
        boolean result;
        logger.info("Trying to edit price to product : {}", productId);
        try {
            result = marketFacade.setProductPrice(username, storeNameId,productId, productPrice);
            if(result){
                return new ResponseEntity<>("Success editing price to product", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to edit price to product : {}", e.getMessage(),productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit price of product : {}",productId);
        return new ResponseEntity<>("Success editing price to product", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductQuantity(String username, String storeNameId, int productId, int productQuantity){
        boolean result;
        logger.info("Trying to edit quantity to product : {}", productId);
        try {
            result = marketFacade.setProductQuantity(username, storeNameId,productId, productQuantity);
            if(result){
                return new ResponseEntity<>("Success editing quantity to product", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to edit quantity to product : {}", e.getMessage(),productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit quantity of product : {}",productId);
        return new ResponseEntity<>("Success editing quantity to product", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setRating(String username, String storeNameId, int productId, int rating){
        boolean result;
        logger.info("Trying to edit rating to product : {}", productId);
        try {
            result = marketFacade.setRating(username, storeNameId,productId,rating);
            if(result){
                return new ResponseEntity<>("Success editing rating to product", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to edit rating to product : {}", e.getMessage(),productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit rating of product : {}",productId);
        return new ResponseEntity<>("Success editing rating to product", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setCategory(String username, String storeNameId, int productId, int category){
        boolean result;
        logger.info("Trying to edit category to product : {}", productId);
        try {
            result = marketFacade.setCategory(username, storeNameId,productId,category);
            if(result){
                return new ResponseEntity<>("Success editing category to product", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to edit category to product : {}", e.getMessage(),productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit category of product : {}",productId);
        return new ResponseEntity<>("Success editing category to product", HttpStatus.OK);
    }
    @Override
    public String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName)
    {
        String historyPurchases;
        logger.info("{} trying to get history purchases from store {} by {}",userName, storeName, customerUserName);
        try {
            historyPurchases = marketFacade.getHistoryPurchasesByCustomer(userName, storeName, customerUserName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , {} Failed to get history purchases from store {} by {}.", e.getMessage(), userName, storeName, customerUserName);
            return "";
        }
        logger.info("FINISHED get history purchases by customer");
        return historyPurchases;
    }

    @Override
    public String getAllHistoryPurchases(String userName, String storeName)
    {
        String historyPurchases;
        logger.info("{} trying to get all history purchases from store {}",userName, storeName);
        try {
            historyPurchases = marketFacade.getAllHistoryPurchases(userName, storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , {} Failed to get all history purchases from store {} .", e.getMessage(), userName, storeName);
            return "";
        }
        logger.info("FINISHED get all history purchases.");
        return historyPurchases;
    }

    public String requestInformationAboutOfficialsInStore(String userName, String storeName)
    {
        String informationAboutOfficials;
        logger.info("{} trying to get informations about officials from store {}",userName, storeName);
        try {
            informationAboutOfficials = marketFacade.requestInformationAboutOfficialsInStore(userName, storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , {} Failed to get informations about officials from store {} .", e.getMessage(), userName, storeName);
            return "";
        }
        logger.info("FINISHED get all informations about officials.");
        return informationAboutOfficials;
    }

    public String requestManagersPermissions(String userName, String storeName)
    {
        String managerPermissions;
        logger.info("{} trying to get managers permissions from store {}",userName, storeName);
        try {
            managerPermissions = marketFacade.requestManagersPermissions(userName, storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , {} Failed to get managers permissions from store {} .", e.getMessage(), userName, storeName);
            return "";
        }
        logger.info("FINISHED get all managers permissions.");
        return managerPermissions;
    }

    public String requestInformationAboutSpecificOfficialInStore(String userName, String storeName, String officialUserName)
    {
        String officialInformation;
        logger.info("{} trying to get information about {} from store {}",userName, officialUserName, storeName);
        try {
            officialInformation = marketFacade.requestInformationAboutSpecificOfficialInStore(userName, storeName, officialUserName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , {} Failed to get information about {} from store {} .", e.getMessage(), userName, officialUserName, storeName);
            return "";
        }
        logger.info("FINISHED get infomation about {} from store {}.", officialUserName, storeName);
        return officialInformation;
    }




}