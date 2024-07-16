package com.example.trading_system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.trading_system.service.Security.generateToken;

public class TradingSystemInitializer {
    @PostConstruct
    public void initializeSystem() {
        Map<String, String> tokens=new HashMap<>();

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
                // Example: userService.register(arguments);
                break;
            case "openSystem":
                // Example: systemService.openSystem(arguments);
                break;
            // Add cases for other use cases...
            default:
                break;
        }
    }
}
