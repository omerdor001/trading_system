package com.example.trading_system.domain.stores;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Entity
@Table(name = "product_lotteries")
public class ProductLottery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "user_probabilities", joinColumns = @JoinColumn(name = "lottery_id"))
    @MapKeyColumn(name = "user_name")
    @Column(name = "probability")
    private Map<String, Double> userProbabilities = new HashMap<>();

    @Column(name = "target_date", nullable = false)
    private LocalDateTime targetDate;

    @Column(name = "price", nullable = false)
    private double price;

    public ProductLottery() {
        // Default constructor for JPA
    }

    public ProductLottery(LocalDateTime targetDate, double price) {
        this.targetDate = targetDate;
        this.price = price;
    }

    public boolean buyLotteryProductTicket(String userName, double price) throws Exception {
        double totalProbabilities = userProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalProbabilities >= 1)
            throw new Exception("Can't buy ticket, it arrived at maximum.");
        else if (totalProbabilities + price / this.price > 1)
            throw new Exception("Can't buy ticket at this price, maximum is " + (1 - totalProbabilities) * this.price);

        userProbabilities.put(userName, userProbabilities.getOrDefault(userName, 0.0) + price / this.price);

        return (totalProbabilities + price / this.price) == 1;
    }

    public String makeLotteryOnProduct() {
        Random random = new Random();
        double randomNumber = random.nextDouble();

        double cumulativeProbability = 0.0;
        for (Map.Entry<String, Double> entry : userProbabilities.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomNumber < cumulativeProbability) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Double> getUserProbabilities() {
        return userProbabilities;
    }

    public void setUserProbabilities(Map<String, Double> userProbabilities) {
        this.userProbabilities = userProbabilities;
    }

    public LocalDateTime getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDateTime targetDate) {
        this.targetDate = targetDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
