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
                restTemplate.getForEntity("/api/trading/suggestManage", String.class, paramsSuggestManager);
                break;
            case "logout":
                break;
            case "exit":
                break;
            case "approveOwner":
                break;
            case "approveManage":
                break;
            case "addProduct":
                break;
            case "addToCart":
                break;
            case "approvePurchase":
                break;
            default:
                break;
        }
    }
}
