package com.example.trading_system.service;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MarketServiceImp implements MarketService {
    public MarketFacade marketFacade;
    private static final Logger logger = LoggerFactory.getLogger(MarketServiceImp.class);

    public MarketServiceImp(MarketFacade marketFacade) {
        this.marketFacade = marketFacade;
    }

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

    public String getStoreProducts(String store_name) {
        String result;
        logger.info("Trying to Gather ALL Store Products");
        try {
            result = marketFacade.getStoreProducts(store_name);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Store Products Info with Store Id : {} ", e.getMessage(), store_name);
            return "";
        }
        logger.info("FINISHED Gather ALL Store Products Info");
        return result;
    }

    public String getProductInfo(String store_name, int product_Id) {
        String result;
        logger.info("Trying to Gather Product Info with Store Id : {} and product ID: {}", store_name, product_Id);
        try {
            result = marketFacade.getProductInfo(store_name, product_Id);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Product Info with Store Id : {} and product ID:{}", e.getMessage(), store_name, product_Id);
            return "";
        }
        logger.info("FINISHED Gather Product Info");
        return result;
    }

    //search in specific store
    public String searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        String result;
        logger.info("Trying to search products in store : {} with name : {}", store_name, name);
        try {
            result = marketFacade.searchNameInStore(name, store_name, minPrice, maxPrice, minRating, category);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with name : {}}", e.getMessage(), store_name, name);
            return "";
        }
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    public String searchCategoryInStore(Category category, String store_name, Double minPrice, Double maxPrice, Double minRating) {
        String result;
        logger.info("Trying to search products in store : {} with category, : {}", store_name, category);
        try {
            result = marketFacade.searchCategoryInStore(category, store_name, minPrice, maxPrice, minRating);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with category : {}}", e.getMessage(), store_name, category);
            return "";
        }
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    public String searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        String result;
        logger.info("Trying to search products in store : {} with keyWords,  : {}", store_name, keyWords);
        try {
            result = marketFacade.searchKeywordsInStore(keyWords, store_name, minPrice, maxPrice, minRating, category);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with keyWords,  : {}}", e.getMessage(), store_name, keyWords);
            return "";
        }
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    //search in stores
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

    public ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                             double product_price, int product_quantity, double rating, Category category, List<String> keyWords){
        boolean result;
        logger.info("Trying to add products to store : {}", store_name);
        try {
            result = marketFacade.addProduct(username,product_id,store_name,product_name,product_description,product_price,product_quantity,rating,category,keyWords);
            if(result){
                return new ResponseEntity<>("Success adding products", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  while trying to add products to store : {}", e.getMessage(),store_name);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished add products to store : {}",store_name);
        return new ResponseEntity<>("Success adding products", HttpStatus.OK);
    }

    public ResponseEntity<String> removeProduct(String username, String store_name, int product_id){
        boolean result;
        logger.info("Trying to remove products to store : {}", store_name);
        try {
            result = marketFacade.removeProduct(username,store_name,product_id);
            if(result){
                return new ResponseEntity<>("Success removing products", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to remove products to store : {}", e.getMessage(),store_name);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished remove products to store : {}",store_name);
        return new ResponseEntity<>("Success removing products", HttpStatus.OK);
    }

    public ResponseEntity<String> setProduct_name(String username,String store_name_id,int productId,String product_name){
        boolean result;
        logger.info("Trying to edit name to product : {}", productId);
        try {
            result = marketFacade.setProduct_name(username,store_name_id,productId,product_name);
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

    public ResponseEntity<String> setProduct_description(String username,String store_name_id,int productId,String product_description){
        boolean result;
        logger.info("Trying to edit description to product : {}", productId);
        try {
            result = marketFacade.setProduct_description(username,store_name_id,productId,product_description);
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

    public ResponseEntity<String> setProduct_price(String username,String store_name_id,int productId,int product_price){
        boolean result;
        logger.info("Trying to edit price to product : {}", productId);
        try {
            result = marketFacade.setProduct_price(username,store_name_id,productId,product_price);
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

    public ResponseEntity<String> setProduct_quantity(String username,String store_name_id,int productId,int product_quantity){
        boolean result;
        logger.info("Trying to edit quantity to product : {}", productId);
        try {
            result = marketFacade.setProduct_quantity(username,store_name_id,productId,product_quantity);
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

    public ResponseEntity<String> setRating(String username,String store_name_id,int productId,int rating){
        boolean result;
        logger.info("Trying to edit rating to product : {}", productId);
        try {
            result = marketFacade.setRating(username,store_name_id,productId,rating);
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

    public ResponseEntity<String> setCategory(String username,String store_name_id,int productId,Category category){
        boolean result;
        logger.info("Trying to edit category to product : {}", productId);
        try {
            result = marketFacade.setCategory(username,store_name_id,productId,category);
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


}