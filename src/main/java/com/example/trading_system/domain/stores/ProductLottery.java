package com.example.trading_system.domain.stores;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProductLottery {

    private HashMap<String, Double> userProbabilites;
    private LocalDateTime targetDate;
    private double price;

    public ProductLottery(LocalDateTime targetDate, double price){
        this.targetDate = targetDate;
        this.price = price;
        this.userProbabilites = new HashMap<>();
    }


    public boolean buyLotteryProductTicket(String userName, double price) throws Exception{

        double totalProbabilities = userProbabilites.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalProbabilities >=1 )
            throw new Exception("Cant buy ticket, it arrived to maximum.");
        else if(totalProbabilities + price/this.price > 1)
            throw new Exception("Cant buy ticket in this price, maximum is " + (1-totalProbabilities) * this.price);

        if (userProbabilites.containsKey(userName))
                this.userProbabilites.put(userName, this.userProbabilites.get(userName) + price/this.price);
        else
                this.userProbabilites.put(userName, price/this.price);

        return (totalProbabilities + price/this.price) == 1;
    }

    public String makeLotteryOnProduct()
    {
        Random random = new Random();
        double randomNumber = random.nextDouble();

        // Determine the winner
        double cumulativeProbability = 0.0;
        for (Map.Entry<String, Double> entry : userProbabilites.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomNumber < cumulativeProbability) {
                return entry.getKey();
            }
        }
        return null;
    }
}
