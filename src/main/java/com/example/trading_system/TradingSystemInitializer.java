package com.example.trading_system;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.trading_system.service.Security.generateToken;

public class TradingSystemInitializer {
    @Autowired
    private RestTemplate restTemplate;
    private Map<String, String> tokens=new HashMap<>();
    @PostConstruct
    public void initializeSystem() {
        tokens=new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/initialState.json");
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> useCases = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> useCase : useCases) {
                String useCaseName = (String) useCase.get("useCase");
                Map<String, Object> arguments = (Map<String, Object>) useCase.get("arguments");
                executeUseCase(useCaseName, arguments);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> generateTokens(){
        Map<String, String> tokens=new HashMap<>();
        String tokenForRegisteredJohn=generateToken("john_doe");
        String tokenForRegisteredJane=generateToken("jane_doe");
        String tokenForRegisteredScooby=generateToken("scooby_doe");
        tokens.put("john_doe",tokenForRegisteredJohn);
        tokens.put("jane_doe",tokenForRegisteredJane);
        tokens.put("scooby_doe",tokenForRegisteredScooby);
        for(int i=0;i<10;i++){
            String username="v"+i;
            String tokenForVisitor=generateToken(username);
            tokens.put(username,tokenForVisitor);
        }
        return tokens;
    }

    private void executeUseCase(String useCaseName, Map<String, Object> arguments) {
        switch (useCaseName) {
            case "register":
                LocalDate birthday = (LocalDate) arguments.get("birthday");
                MultiValueMap<String, String> paramsRegister = new LinkedMultiValueMap<>();
                paramsRegister.add("username", (String) arguments.get("username"));
                paramsRegister.add("password", (String) arguments.get("password"));
                paramsRegister.add("birthday", birthday.toString());
                restTemplate.getForEntity("/api/trading/register", String.class, paramsRegister);
                break;
            case "openSystem":
                restTemplate.postForEntity("/api/trading/openSystem", null, String.class);
                break;
            case "enter":
                restTemplate.postForEntity("/api/trading/enter", null, String.class);
                break;
            case "login0":
                MultiValueMap<String, String> paramsLogin0 = new LinkedMultiValueMap<>();
                paramsLogin0.add("token", tokens.get("v0"));
                paramsLogin0.add("usernameV", (String) arguments.get("usernameV"));
                paramsLogin0.add("username", (String) arguments.get("username"));
                paramsLogin0.add("password", (String) arguments.get("password"));
                restTemplate.getForEntity("/api/trading/login", String.class, paramsLogin0);
                break;
            case "login2":
                MultiValueMap<String, String> paramsLogin2 = new LinkedMultiValueMap<>();
                paramsLogin2.add("token", tokens.get("v2"));
                paramsLogin2.add("usernameV", (String) arguments.get("usernameV"));
                paramsLogin2.add("username", (String) arguments.get("username"));
                paramsLogin2.add("password", (String) arguments.get("password"));
                restTemplate.getForEntity("/api/trading/login", String.class, paramsLogin2);
                break;
            case "login4":
                MultiValueMap<String, String> paramsLogin4 = new LinkedMultiValueMap<>();
                paramsLogin4.add("token", tokens.get("v4"));
                paramsLogin4.add("usernameV", (String) arguments.get("usernameV"));
                paramsLogin4.add("username", (String) arguments.get("username"));
                paramsLogin4.add("password", (String) arguments.get("password"));
                restTemplate.getForEntity("/api/trading/login", String.class, paramsLogin4);
                break;
            case "login7":
                MultiValueMap<String, String> paramsLogin7 = new LinkedMultiValueMap<>();
                paramsLogin7.add("token", tokens.get("v7"));
                paramsLogin7.add("usernameV", (String) arguments.get("usernameV"));
                paramsLogin7.add("username", (String) arguments.get("username"));
                paramsLogin7.add("password", (String) arguments.get("password"));
                restTemplate.getForEntity("/api/trading/login", String.class, paramsLogin7);
                break;
            case "openStore":
                MultiValueMap<String, String> paramsOpenStore = new LinkedMultiValueMap<>();
                paramsOpenStore.add("username", (String) arguments.get("username"));
                paramsOpenStore.add("token", tokens.get("john_doe"));
                paramsOpenStore.add("storeName", (String) arguments.get("storeName"));
                paramsOpenStore.add("description", (String) arguments.get("description"));
                restTemplate.getForEntity("/api/trading/create-store", String.class, paramsOpenStore);
                break;
            case "suggestOwner":
                MultiValueMap<String, String> paramsSuggestOwner = new LinkedMultiValueMap<>();
                paramsSuggestOwner.add("appoint", (String) arguments.get("appoint"));
                paramsSuggestOwner.add("token", tokens.get("john_doe"));
                paramsSuggestOwner.add("newOwner", (String) arguments.get("newOwner"));
                paramsSuggestOwner.add("storeName", (String) arguments.get("storeName"));
                restTemplate.getForEntity("/api/trading/suggestOwner", String.class, paramsSuggestOwner);
                break;
            case "suggestManage":
                MultiValueMap<String, String> paramsSuggestManager = new LinkedMultiValueMap<>();
                paramsSuggestManager.add("appoint", (String) arguments.get("appoint"));
                paramsSuggestManager.add("token", tokens.get("john_doe"));
                paramsSuggestManager.add("newManager", (String) arguments.get("newManager"));
                paramsSuggestManager.add("storeName", (String) arguments.get("storeName"));
                paramsSuggestManager.add("watch", String.valueOf(arguments.get("watch")));
                paramsSuggestManager.add("editSupply", String.valueOf(arguments.get("editSupply")));
                paramsSuggestManager.add("editBuyPolicy", String.valueOf(arguments.get("editBuyPolicy")));
                paramsSuggestManager.add("editDiscountPolicy", String.valueOf(arguments.get("editDiscountPolicy")));
                paramsSuggestManager.add("acceptBids", String.valueOf(arguments.get("acceptBids")));
                restTemplate.getForEntity("/api/trading/suggestManage", String.class, paramsSuggestManager);
                break;
            case "logoutR0":
                MultiValueMap<String, String> paramsLogoutR0 = new LinkedMultiValueMap<>();
                paramsLogoutR0.add("token", tokens.get("john_doe"));
                paramsLogoutR0.add("username", (String) arguments.get("username"));
                restTemplate.getForEntity("/api/trading/logout", String.class, paramsLogoutR0);
                break;
            case "logoutR1":
                MultiValueMap<String, String> paramsLogoutR1 = new LinkedMultiValueMap<>();
                paramsLogoutR1.add("token", tokens.get("jane_doe"));
                paramsLogoutR1.add("username", (String) arguments.get("username"));
                restTemplate.getForEntity("/api/trading/logout", String.class, paramsLogoutR1);
                break;
            case "logoutR2":
                MultiValueMap<String, String> paramsLogoutR2 = new LinkedMultiValueMap<>();
                paramsLogoutR2.add("token", tokens.get("scooby_doe"));
                paramsLogoutR2.add("username", (String) arguments.get("username"));
                restTemplate.getForEntity("/api/trading/logout", String.class, paramsLogoutR2);
                break;
            case "exit1":
                MultiValueMap<String, String> paramsExit1 = new LinkedMultiValueMap<>();
                paramsExit1.add("token", tokens.get("v1"));
                paramsExit1.add("username", (String) arguments.get("username"));
                restTemplate.getForEntity("/api/trading/exit", String.class, paramsExit1);
                break;
            case "exit3":
                MultiValueMap<String, String> paramsExit3 = new LinkedMultiValueMap<>();
                paramsExit3.add("token", tokens.get("v3"));
                paramsExit3.add("username", (String) arguments.get("username"));
                restTemplate.getForEntity("/api/trading/exit", String.class, paramsExit3);
                break;
            case "exit5":
                MultiValueMap<String, String> paramsExit5 = new LinkedMultiValueMap<>();
                paramsExit5.add("token", tokens.get("v5"));
                paramsExit5.add("username", (String) arguments.get("username"));
                restTemplate.getForEntity("/api/trading/exit", String.class, paramsExit5);
                break;
            case "exit6":
                MultiValueMap<String, String> paramsExit6 = new LinkedMultiValueMap<>();
                paramsExit6.add("token", tokens.get("v6"));
                paramsExit6.add("username", (String) arguments.get("username"));
                restTemplate.getForEntity("/api/trading/exit", String.class, paramsExit6);
                break;
            case "exit8":
                MultiValueMap<String, String> paramsExit8 = new LinkedMultiValueMap<>();
                paramsExit8.add("token", tokens.get("v8"));
                paramsExit8.add("username", (String) arguments.get("username"));
                restTemplate.getForEntity("/api/trading/exit", String.class, paramsExit8);
                break;
            case "approveOwner":
                MultiValueMap<String, String> paramsApproveOwner = new LinkedMultiValueMap<>();
                paramsApproveOwner.add("newOwner", (String) arguments.get("newOwner"));
                paramsApproveOwner.add("token", tokens.get("jane_doe"));
                paramsApproveOwner.add("storeName", (String) arguments.get("storeName"));
                paramsApproveOwner.add("appoint", (String) arguments.get("appoint"));
                restTemplate.postForEntity("/api/trading/approveOwner", paramsApproveOwner, String.class);
                break;
            case "approveManage":
                MultiValueMap<String, String> paramsApproveManage = new LinkedMultiValueMap<>();
                paramsApproveManage.add("newManager", (String) arguments.get("newManager"));
                paramsApproveManage.add("token", tokens.get("scooby_doe"));
                paramsApproveManage.add("storeName", (String) arguments.get("store_name_id"));
                paramsApproveManage.add("appoint", (String) arguments.get("appoint"));
                paramsApproveManage.add("watch", String.valueOf(arguments.get("watch")));
                paramsApproveManage.add("editSupply", String.valueOf(arguments.get("editSupply")));
                paramsApproveManage.add("editBuyPolicy", String.valueOf(arguments.get("editBuyPolicy")));
                paramsApproveManage.add("editDiscountPolicy", String.valueOf(arguments.get("editDiscountPolicy")));
                paramsApproveManage.add("acceptBids", String.valueOf(arguments.get("acceptBids")));
                restTemplate.postForEntity("/api/trading/approveManage", paramsApproveManage, String.class);
                break;
            case "addProduct":
                MultiValueMap<String, String> paramsAddProduct = new LinkedMultiValueMap<>();
                paramsAddProduct.add("username", (String) arguments.get("username"));
                paramsAddProduct.add("token", tokens.get("scooby_doe"));
                paramsAddProduct.add("productId", String.valueOf(arguments.get("product_id")));
                paramsAddProduct.add("storeName", (String) arguments.get("store_name"));
                paramsAddProduct.add("productName", (String) arguments.get("product_name"));
                paramsAddProduct.add("productDescription", (String) arguments.get("product_description"));
                paramsAddProduct.add("productPrice", String.valueOf(arguments.get("product_price")));
                paramsAddProduct.add("productQuantity", String.valueOf(arguments.get("product_quantity")));
                paramsAddProduct.add("rating", String.valueOf(arguments.get("rating")));
                paramsAddProduct.add("category", String.valueOf(arguments.get("category")));
                paramsAddProduct.add("keyWords", (String) arguments.get("keyWords"));
                restTemplate.postForEntity("/api/trading/product/add", paramsAddProduct, String.class);
                break;
            case "addToCart1":
                MultiValueMap<String, String> paramsAddToCart1 = new LinkedMultiValueMap<>();
                paramsAddToCart1.add("username", (String) arguments.get("username"));
                paramsAddToCart1.add("token", tokens.get("v6"));
                paramsAddToCart1.add("productId", String.valueOf(arguments.get("productId")));
                paramsAddToCart1.add("storeName", (String) arguments.get("storeName"));
                paramsAddToCart1.add("quantity", String.valueOf(arguments.get("quantity")));
                paramsAddToCart1.add("price", String.valueOf(arguments.get("price")));
                restTemplate.postForEntity("/api/trading/cart/add", paramsAddToCart1, String.class);
                break;
            case "addToCart2":
                MultiValueMap<String, String> paramsAddToCart2 = new LinkedMultiValueMap<>();
                paramsAddToCart2.add("username", (String) arguments.get("username"));
                paramsAddToCart2.add("token", tokens.get("jane_doe"));
                paramsAddToCart2.add("productId", String.valueOf(arguments.get("productId")));
                paramsAddToCart2.add("storeName", (String) arguments.get("storeName"));
                paramsAddToCart2.add("quantity", String.valueOf(arguments.get("quantity")));
                paramsAddToCart2.add("price", String.valueOf(arguments.get("price")));
                restTemplate.postForEntity("/api/trading/cart/add", paramsAddToCart2, String.class);
                break;
            case "approvePurchase1":
                MultiValueMap<String, String> paramsApprovePurchase1 = new LinkedMultiValueMap<>();
                paramsApprovePurchase1.add("username", (String) arguments.get("username"));
                paramsApprovePurchase1.add("token", tokens.get("v6"));
                paramsApprovePurchase1.add("address", (String) arguments.get("address"));
                paramsApprovePurchase1.add("amount", (String) arguments.get("amount"));
                paramsApprovePurchase1.add("currency", (String) arguments.get("currency"));
                paramsApprovePurchase1.add("cardNumber", (String) arguments.get("cardNumber"));
                paramsApprovePurchase1.add("month", (String) arguments.get("month"));
                paramsApprovePurchase1.add("year", (String) arguments.get("year"));
                paramsApprovePurchase1.add("holder", (String) arguments.get("holder"));
                paramsApprovePurchase1.add("ccv", (String) arguments.get("ccv"));
                paramsApprovePurchase1.add("id", (String) arguments.get("id"));
                restTemplate.postForEntity("/api/trading/purchase/approve", paramsApprovePurchase1, String.class);
                break;
            case "approvePurchase2":
                MultiValueMap<String, String> paramsApprovePurchase2 = new LinkedMultiValueMap<>();
                paramsApprovePurchase2.add("username", (String) arguments.get("username"));
                paramsApprovePurchase2.add("token", tokens.get("jane_doe"));
                paramsApprovePurchase2.add("address", (String) arguments.get("address"));
                paramsApprovePurchase2.add("amount", (String) arguments.get("amount"));
                paramsApprovePurchase2.add("currency", (String) arguments.get("currency"));
                paramsApprovePurchase2.add("cardNumber", (String) arguments.get("cardNumber"));
                paramsApprovePurchase2.add("month", (String) arguments.get("month"));
                paramsApprovePurchase2.add("year", (String) arguments.get("year"));
                paramsApprovePurchase2.add("holder", (String) arguments.get("holder"));
                paramsApprovePurchase2.add("ccv", (String) arguments.get("ccv"));
                paramsApprovePurchase2.add("id", (String) arguments.get("id"));
                restTemplate.postForEntity("/api/trading/purchase/approve", paramsApprovePurchase2, String.class);
                break;
            default:
                break;
        }
    }
}
