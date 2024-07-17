package com.example.trading_system;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static com.example.trading_system.service.Security.generateToken;

@Service
public class TradingSystemInitializer {
    private Map<String, String> tokens;
    @Autowired
    private TradingSystemRestController tradingSystemRestController;
    @PostConstruct
    public void initializeSystem() {
        File file = new File("C:\\Users\\alex\\Documents\\trading_system\\initialState.json");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> useCases = objectMapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
            Set<String> usernames=new HashSet<>();
            for (Map<String, Object> useCase : useCases) {
                Map<String, Object> arguments = (Map<String, Object>) useCase.get("arguments");
                usernames.addAll(extractUsernames(arguments));
            }
            tokens=generateTokens(usernames);
            for (Map<String, Object> useCase : useCases) {
                String useCaseName = (String) useCase.get("useCase");
                Map<String, Object> arguments = (Map<String, Object>) useCase.get("arguments");
                executeUseCase(useCaseName, arguments);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<String> extractUsernames(Map<String, Object> arguments){
        Set<String> usernames=new HashSet<>();
        if (arguments.containsKey("username")) {
            usernames.add((String) arguments.get("username"));
        }
        if (arguments.containsKey("appoint")) {
            usernames.add((String) arguments.get("appoint"));
        }
        if (arguments.containsKey("newOwner")) {
            usernames.add((String) arguments.get("newOwner"));
        }
        if (arguments.containsKey("newManager")) {
            usernames.add((String) arguments.get("newManager"));
        }
        if (arguments.containsKey("usernameV")) {
            usernames.add((String) arguments.get("usernameV"));
        }
        return usernames;
    }

    private Map<String, String> generateTokens(Set<String> usernames){
        Map<String, String> tokens=new HashMap<>();
        for(String username:usernames){
            if(username.charAt(0)!='v'){
                tokens.put("r"+username,generateToken("r"+username));
            }
            else{
                tokens.put(username,generateToken(username));
            }
        }
        return tokens;
    }

    private void executeUseCase(String useCaseName, Map<String, Object> arguments) {
        ResponseEntity<String> response;
        switch (useCaseName) {
            case "register":
                response=tradingSystemRestController.register((String) arguments.get("username"),(String) arguments.get("password"),LocalDate.parse((String) arguments.get("birthdate")));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "openSystem":
                response=tradingSystemRestController.openSystem();
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "enter":
                response=tradingSystemRestController.enter();
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "login":
                response=tradingSystemRestController.login(tokens.get((String) arguments.get("usernameV")),(String) arguments.get("usernameV"),(String) arguments.get("username"),(String) arguments.get("password"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "openStore":
                response=tradingSystemRestController.openStore((String) arguments.get("username"),tokens.get((String) arguments.get("username")),(String) arguments.get("storeName"),(String) arguments.get("description"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "suggestOwner":
                response=tradingSystemRestController.suggestOwner((String) arguments.get("appoint"),tokens.get((String) arguments.get("appoint")),(String) arguments.get("newOwner"),(String) arguments.get("storeName"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "suggestManage":
                response=tradingSystemRestController.suggestManage((String) arguments.get("appoint"),tokens.get((String) arguments.get("appoint")),(String) arguments.get("newManager"),(String) arguments.get("store_name_id"),(Boolean) arguments.get("watch"),
                        (Boolean) arguments.get("editSupply"),(Boolean) arguments.get("editBuyPolicy"),(Boolean) arguments.get("editDiscountPolicy"),(Boolean) arguments.get("acceptBids"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "logout":
                response=tradingSystemRestController.logout(tokens.get((String) arguments.get("username")),(String) arguments.get("username"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "exit":
                response=tradingSystemRestController.exit(tokens.get((String) arguments.get("username")),(String) arguments.get("username"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "approveOwner":
                response=tradingSystemRestController.approveOwner((String) arguments.get("newOwner"),tokens.get((String) arguments.get("newOwner")),(String) arguments.get("storeName"),(String) arguments.get("appoint"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "approveManage":
                response=tradingSystemRestController.approveManage((String) arguments.get("newManager"),tokens.get((String) arguments.get("newManager")), (String) arguments.get("store_name_id"),(String) arguments.get("appoint"),
                        (Boolean) arguments.get("watch"), (Boolean) arguments.get("editSupply"),(Boolean) arguments.get("editBuyPolicy"),(Boolean) arguments.get("editDiscountPolicy"),(Boolean) arguments.get("acceptBids"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "addProduct":
                response=tradingSystemRestController.addProduct((String) arguments.get("username"),tokens.get((String) arguments.get("username")),(Integer) arguments.get("product_id"),(String) arguments.get("store_name"),
                        (String) arguments.get("product_name"),(String) arguments.get("product_description"),Double.valueOf(arguments.get("product_price").toString()),(Integer) arguments.get("product_quantity"),
                        Double.valueOf(arguments.get("rating").toString()),(Integer) arguments.get("category"),(String) arguments.get("keyWords"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "addToCart":
                response=tradingSystemRestController.addToCart((String) arguments.get("username"),tokens.get((String) arguments.get("username")),(Integer) arguments.get("productId"),(String) arguments.get("storeName"),
                        (Integer) arguments.get("quantity"),Double.valueOf(arguments.get("price").toString()));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            case "approvePurchase":
                response=tradingSystemRestController.approvePurchase((String) arguments.get("username"),tokens.get((String) arguments.get("username")),(String) arguments.get("address"),(String) arguments.get("amount"),
                        (String) arguments.get("currency"),(String) arguments.get("cardNumber"),(String) arguments.get("month"),(String) arguments.get("year"),
                        (String) arguments.get("holder"),(String) arguments.get("ccv"),(String) arguments.get("id"));
                if(response.getStatusCode().value()!=200){
                    System.out.println(response.getBody());
                }
                break;
            default:
                break;
        }
    }
}
