package com.example.trading_system.domain.stores;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;
/**
 * A buying policy defines who is allowed to buy products in the store, what are the possible purchase routes,
 * And what are the rules that apply to them.
 * <br>For example, minimum items per product per purchase. Purchase policy
 * Defined for a store and can apply to products or customers or both.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class BuyPolicy {
    // Inner class to define rules for products
    @Getter
    @Setter
    public static class ProductRule {
        private String productId;
        private int minimumItemsPerPurchase;

        public ProductRule(String productId, int minimumItemsPerPurchase) {
            this.productId = productId;
            this.minimumItemsPerPurchase = minimumItemsPerPurchase;
        }
    }

    // Inner class to define rules for customers
    @Setter
    @Getter
    public static class CustomerRule {
        private String customerId;
        private List<String> allowedProducts;

        public CustomerRule(String customerId, List<String> allowedProducts) {
            this.customerId = customerId;
            this.allowedProducts = allowedProducts;
        }

    }

    // Fields for the buying policy
    private List<ProductRule> productRules;
    private List<CustomerRule> customerRules;
    private Map<String, String> purchaseRoutes;

//    public BuyPolicy(List<ProductRule> productRules, List<CustomerRule> customerRules, Map<String, String> purchaseRoutes) {
//        this.productRules = productRules;
//        this.customerRules = customerRules;
//        this.purchaseRoutes = purchaseRoutes;
//    }

    // Method to check if a purchase is allowed
    public boolean isPurchaseAllowed(String customerId, String productId, int quantity) {

        return productRules.stream().noneMatch(productRule -> productRule.getProductId().equals(productId) && quantity < productRule.getMinimumItemsPerPurchase()) && customerRules.stream().noneMatch(customerRule -> customerRule.getCustomerId().equals(customerId) && !customerRule.getAllowedProducts().contains(productId));
    }

}



