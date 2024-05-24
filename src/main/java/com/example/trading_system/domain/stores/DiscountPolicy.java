package com.example.trading_system.domain.stores;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 *Discount types:
 * There are several types of possible discounts: <br><br>
 * Visible discount: consists of a discount percentage and discount duration, and can refer to one product, group a.
 * products, or apply to all products in the store. During the discount period, the price of the product(s)
 * On which the discount applies, is shown to the buyers after the discount, when in addition, the buyers also see the
 * The original price and the end date of the discount. In addition, an express discount can also include a condition
 * and 30% discount x to receive the discount (conditional discount)<br> for example: 20% discount if you buy a product
 * together. y- and x in the purchase of the products
 * <br><br>
 * Hidden discount: consists of discount percentage, discount duration, and discount code. In contrast to the types of assumptions b
 * Previously, this discount was not visible to buyers. A buyer can enter a discount code at the time of purchase, and if
 * The discount code is valid and defined for the product, the buyer receives the stated discount
 */
public class DiscountPolicy {

    // Base Discount class
    @Setter
    @Getter
    public abstract static class Discount {
        protected double discountPercentage;
        protected LocalDate startDate;
        protected LocalDate endDate;

        Discount(double discountPercentage, LocalDate startDate, LocalDate endDate) {
            this.discountPercentage = discountPercentage;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public abstract boolean isApplicable(Product product, int quantity);
    }

    // Visible Discount class
    public static class VisibleDiscount extends Discount {
        private List<String> applicableProductIds;
        private Map<String, String> conditions;

        public VisibleDiscount(double discountPercentage, LocalDate startDate, LocalDate endDate, List<String> applicableProductIds, Map<String, String> conditions) {
            super(discountPercentage, startDate, endDate);
            this.applicableProductIds = applicableProductIds;
            this.conditions = conditions;
        }

        public List<String> getApplicableProductIds() {
            return applicableProductIds;
        }

        public void setApplicableProductIds(List<String> applicableProductIds) {
            this.applicableProductIds = applicableProductIds;
        }

        public Map<String, String> getConditions() {
            return conditions;
        }

        public void setConditions(Map<String, String> conditions) {
            this.conditions = conditions;
        }

        @Override
        public boolean isApplicable(Product product, int quantity) {
            if (!applicableProductIds.contains(product.getId())) {
                return false;
            }
            // Check conditions if any
            for (String conditionProductId : conditions.keySet()) {
                String requiredProductId = conditions.get(conditionProductId);
                // Logic to check if condition is met
                // This logic should be defined based on the store's purchase context
            }
            return true;
        }

        public void displayDiscount(Product product) {
            if (isApplicable(product, 1)) { // Assuming 1 quantity to display
//                System.out.println("Original Price: " + product.getPrice());
//                System.out.println("Discounted Price: " + (product.getPrice() * (1 - discountPercentage / 100)));
//                System.out.println("Discount End Date: " + endDate);
            }
        }
    }

    // Hidden Discount class
    @Setter
    @Getter
    public static class HiddenDiscount extends Discount {
        private String discountCode;

        public HiddenDiscount(double discountPercentage, LocalDate startDate, LocalDate endDate, String discountCode) {
            super(discountPercentage, startDate, endDate);
            this.discountCode = discountCode;
        }

        @Override
        public boolean isApplicable(Product product, int quantity) {
            // Hidden discount logic based on discount code validation
            return true;
        }

        public double applyDiscount(Product product, String code) {
            if (code.equals(discountCode) && isApplicable(product, 1)) {
                return product.getPrice() * (1 - discountPercentage / 100);
            }
            return product.getPrice();
        }
    }



//    // Example main method
//    public static void main(String[] args) {
//        // Example usage
//        Product product1 = new Product("p1", 100.0);
//        Product product2 = new Product("p2", 200.0);
//
//        VisibleDiscount visibleDiscount = new VisibleDiscount(20, LocalDate.now(), LocalDate.now().plusDays(10), List.of("p1"), Map.of());
//        HiddenDiscount hiddenDiscount = new HiddenDiscount(10, LocalDate.now(), LocalDate.now().plusDays(10), "DISCOUNT10");
//
//        visibleDiscount.displayDiscount(product1);
//        System.out.println("Price after hidden discount: " + hiddenDiscount.applyDiscount(product1, "DISCOUNT10"));
//    }
}