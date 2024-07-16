package com.example.trading_system.domain.stores;
import java.util.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private int productID;

    @Column(nullable = false)
    private double price;


    @ElementCollection
    @CollectionTable(name = "bid_approvals", joinColumns = @JoinColumn(name = "bid_id"))
    @Column(name = "owner")
    private List<String> approvedBy = new LinkedList<>();

    @Column(nullable = false)
    private boolean allOwnersApproved;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Getter
    @Column(nullable = false)
    private String address;

    @Getter
    @Column(nullable = false)
    private String amount;

    @Getter
    @Column(nullable = false)
    private String currency;

    @Getter
    @Column(nullable = false)
    private String cardNumber;

    @Getter
    @Column(nullable = false)
    private String month;

    @Getter
    @Column(nullable = false)
    private String year;

    @Getter
    @Column(nullable = false)
    private String holder;

    @Getter
    @Column(nullable = false)
    private String ccv;

    @Getter
    @Column(nullable = false)
    private String holderId;

    @Getter
    @Setter
    @Column(nullable = false)
    private boolean customerApproved;

    public Bid(String userName, int productID, double price,String address, String amount, String currency,String cardNumber, String month,String year,String holder,String ccv,String id) {
        this.userName = userName;
        this.productID = productID;
        this.price = price;
        approvedBy = new LinkedList<>();
        allOwnersApproved = false;
        this.address = address;
        this.amount = amount;
        this.currency = currency;
        this.cardNumber = cardNumber;
        this.month = month;
        this.year = year;
        this.holder = holder;
        this.ccv = ccv;
        this.holderId = id;
        this.customerApproved = true;
    }

    public Bid() {

    }


    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getUserName() {
        return userName;
    }

    public int getProductID() {
        return productID;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getApprovedBy() {
        return approvedBy;
    }


    public void approveBid(String userName){
        approvedBy.add(userName);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"userName\" : \"").append(userName).append("\",\n");
        sb.append("  \"productID\" : ").append(productID).append(",\n");
        sb.append("  \"price\" : ").append(price).append(",\n");
        sb.append("  \"allOwnersApproved\" : ").append(allOwnersApproved).append(",\n");
        sb.append("  \"customerApproved\" : ").append(customerApproved).append(",\n");
        sb.append("  \"approvedBy\" : [");

        for (int i = 0; i < approvedBy.size(); i++) {
            sb.append("\"").append(approvedBy.get(i)).append("\"");
            if (i < approvedBy.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]\n");
        sb.append("}");
        return sb.toString();

    }

    public static String toJsonList(List<Bid> bids) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < bids.size(); i++) {
            sb.append(bids.get(i).toString());
            if (i < bids.size() - 1) {
                sb.append(",\n");
            }
        }

        sb.append("\n]");
        return sb.toString();
    }

    public void setAllOwnersApproved(boolean b) {
        allOwnersApproved = b;
    }
    public boolean getAllOwnersApproved(){
        return allOwnersApproved;
    }
}
