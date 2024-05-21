package com.example.trading_system.Service;

import com.example.trading_system.Domain.stores.MarketFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketService {
    public MarketFacade marketFacade;
    private static final Logger logger= LoggerFactory.getLogger(ExternalServices.class);
    public MarketService(MarketFacade marketFacade) {
        this.marketFacade=marketFacade;
    }

    public String getAllStores()
    {
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

    public String getStoreProducts(String store_name)
    {
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

    public String getProductInfo(String store_name, int product_Id){
        String result;
        logger.info("Trying to Gather Product Info with Store Id : {} and product ID: {}", store_name, product_Id);
        try {
            result= marketFacade.getProductInfo(store_name,product_Id);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Product Info with Store Id : {} and product ID:{}", e.getMessage(), store_name, product_Id);
            return "";
        }
        logger.info("FINISHED Gather Product Info");
        return result;
    }

}
