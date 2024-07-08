package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.Message;
import com.example.trading_system.domain.stores.discountPolicies.*;
import com.example.trading_system.domain.stores.purchasePolicies.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class Store {
    private static final Logger logger = LoggerFactory.getLogger(Store.class);
    private String nameId;
    private String description;
    private HashMap<Integer, Product> products;
    @Getter
    private List<String> managers;
    @Getter
    private List<String> owners;
    private String founder;
    @Getter
    @Setter
    private boolean isActive;
    private boolean isOpen;
    private StoreSalesHistory salesHistory;
    @Getter
    @Setter
    private Double storeRating;
    private LinkedList<DiscountPolicy> discountPolicies;
    private LinkedList<Condition> discountConditions;
    private LinkedList<PurchasePolicy> purchasePolicies;
    private LinkedList<Message> messages;
    private LinkedList<Bid> bids;
    private HashMap<Integer, ProductLottery> lotteryProducts;

    public Store(String nameId, String description, String founder, Double storeRating) {
        this.nameId = nameId;
        this.description = description;
        this.products = new HashMap<>();
        this.isActive = true;
        this.salesHistory = new StoreSalesHistory();
        this.founder = founder;
        this.managers = new LinkedList<>();
        this.owners = new LinkedList<>();
        this.storeRating = storeRating;
        this.discountPolicies = new LinkedList<>();
        this.discountConditions = new LinkedList<>();
        this.purchasePolicies = new LinkedList<>();
        this.isOpen = true;
        this.messages = new LinkedList<>();
        this.bids = new LinkedList<>();
        this.lotteryProducts = new HashMap<>();
    }

    public List<Product> filterProducts(List<Product> productList, Double minPrice, Double maxPrice, Double minRating, int category) {
        return productList.stream().filter(p -> minPrice == null || p.getProduct_price() >= minPrice).filter(p -> maxPrice == null || p.getProduct_price() <= maxPrice).filter(p -> minRating == null || p.getRating() >= minRating).filter(p -> category < 0 || p.getCategory().getIntValue() == category).collect(Collectors.toList());
    }

    public List<Product> searchName(String name, Double minPrice, Double maxPrice, Double minRating, Integer category) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getProduct_name().equals(name)) list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    public List<Product> searchCategory(int category, Double minPrice, Double maxPrice, Double minRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getCategory().getIntValue() == category) list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    public List<Product> searchKeywords(String keyWords, Double minPrice, Double maxPrice, Double minRating, int category) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getKeyWords().contains(keyWords)) list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    public List<Product> filterProducts(List<Product> productList, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        return productList.stream().filter(p -> minPrice == null || p.getProduct_price() >= minPrice).filter(p -> maxPrice == null || p.getProduct_price() <= maxPrice).filter(p -> minRating == null || p.getRating() >= minRating).filter(p -> storeRating == null || p.getRating() >= storeRating).filter(p -> category < 0 || p.getCategory().getIntValue() == category).collect(Collectors.toList());
    }

    public List<Product> searchName(String name, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getProduct_name().equals(name)) list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category, storeRating);
    }

    public List<Product> searchCategory(int category, Double minPrice, Double maxPrice, Double minRating, Double storeRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getCategory().getIntValue() == category) list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category, storeRating);
    }

    public List<Product> searchKeywords(String keyWords, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getKeyWords().contains(keyWords)) list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category, storeRating);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"name_id\":\"").append(nameId).append("\"");
        sb.append(", \"description\":\"").append(description).append("\"");
        sb.append(", \"products\":[");

        for (Product p : products.values()) {
            sb.append(p.toString());
            sb.append(", ");

        }

        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    public boolean isProductExist(int productId) {
        return products.containsKey(productId);
    }

    public Product getProduct(int productId) {
        return products.get(productId);
    }

    public synchronized void addProduct(int product_id, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) {
        if (isProductExist(product_id))
            throw new IllegalArgumentException("Product with id " + product_id + " already exists");
        if (product_price < 0) throw new IllegalArgumentException("Price can't be negative number");
        if (product_quantity <= 0) throw new IllegalArgumentException("Quantity must be natural number");
        if (rating < 0) throw new IllegalArgumentException("Rating can't be negative number");
        Product product = new Product(product_id, product_name, product_description, product_price, product_quantity, rating, Category.getCategoryFromInt(category), keyWords);
        product.setStore_name(this.nameId);
        products.put(product.getProduct_id(), product);
    }

    public synchronized void removeProduct(int productId) {
        if (!isProductExist(productId))
            throw new IllegalArgumentException("Product with id " + productId + " does not exist");
        products.remove(productId);
    }

    public synchronized void setProductName(int productId, String product_name) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_name(product_name);
        } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }

    public synchronized void setProductDescription(int productId, String product_description) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_description(product_description);
        } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }

    public synchronized void setProductPrice(int productId, double product_price) {
        if (product_price < 0) throw new IllegalArgumentException("Price can't be negative number");
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_price(product_price);
        } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");

    }

    public synchronized void setProductQuantity(int productId, int product_quantity) {
        if (product_quantity <= 0) throw new IllegalArgumentException("Quantity must be natural number");
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_quantity(product_quantity);
        } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }

    public synchronized void setRating(int productId, double rating) {
        if (rating < 0) throw new IllegalArgumentException("Rating can't be negative number");
        Product product = getProduct(productId);
        if (product != null) {
            product.setRating(rating);
        } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");

    }

    public synchronized void setCategory(int productId, int category) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setCategory(category);
        } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }

    public  synchronized void addKeyWordToProduct(int productId, String keyword) {
        Product product = getProduct(productId);
        if (product != null) {
            product.addKeyWord(keyword);
        } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }

    public synchronized void removeKeyWordFromProduct(int productId, String keyword) {
        Product product = getProduct(productId);
        if (product != null) {
            product.removeKeyWord(keyword);
        } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }


    List<Purchase> getHistoryPurchasesByCustomer(String customerUsername) {
        return salesHistory.getPurchasesByCustomer(customerUsername);
    }

    List<Purchase> getAllHistoryPurchases() {
        return salesHistory.getAllPurchases();
    }


    public String getNameId() {
        return nameId;
    }

    public HashMap<Integer, Product> getProducts() {     //Added because missing
        return products;
    }

    public void setActive(Boolean active) {             //Added because missing
        this.isActive = active;
    }

    public Boolean isActive() {                      //Added because missing
        return isActive;
    }

    public List<String> getOwners() {                //Added because missing
        return owners;
    }

    public List<String> getManagers() {                //Added because missing
        return managers;
    }

    public boolean isOwnerOfStore(String username){
        return owners.contains(username);
    }

    public boolean isManagerOfStore(String username){
        return managers.contains(username);
    }

    public void addPurchase(Purchase purchase) {
        salesHistory.addPurchase(purchase);
    }

    public boolean isRoleHolder(String userName) {
        return managers.contains(userName) || owners.contains(userName);
    }

    public void addOwner(String userName) {
        owners.add(userName);
    }

    public void addManager(String userName) {
        managers.add(userName);
    }

    public void removeOwner(String userName) {
        owners.remove(userName);
    }

    public void removeManager(String userName) {
        managers.remove(userName);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {            //Added because missing
        isOpen = open;
    }

    public String getFounder() {
        return founder;
    }

    public LinkedList<DiscountPolicy> getDiscountPolicies() {
        return discountPolicies;
    }

    public LinkedList<Condition> getDiscountConditions() {
        return discountConditions;
    }

    public LinkedList<PurchasePolicy> getPurchasePolicies() {
        return purchasePolicies;
    }

    public LinkedList<Message> getMessages(){
        return this.messages;
    }

    public String getMessagesJSON(){
        return Message.toJsonList(this.messages);
    }

    public synchronized void releaseReservedProducts(int productId, int quantity) {
        getProduct(productId).releaseReservedProducts(quantity);
    }

    public synchronized void removeReservedProducts(int productId, int quantity) {
        getProduct(productId).removeReservedProducts(quantity);
    }

    public synchronized void checkAvailabilityAndConditions(int id, int quantity) {
        if (!isOpen()) throw new IllegalArgumentException("When store is closed cant to check product quantity");
        if (getProduct(id) == null) {
            logger.error("Product not found: " + id);
            throw new NoSuchElementException("Product not found: " + id);
        }
        getProduct(id).checkAvailabilityAndConditions(quantity);
    }

    public double calculatePrice(Collection<ProductInSaleDTO> items) {
        double price = 0;
        for (ProductInSaleDTO item : items)
            price += item.getPrice() * item.getQuantity();
        for (DiscountPolicy policy : discountPolicies)
            price -= policy.calculateDiscount(items);
        return price;
    }

    public boolean validatePurchasePolicies(Collection<ProductInSaleDTO> items, int age) {
        for (PurchasePolicy policy : purchasePolicies) {
            if (!policy.isPurchasePolicySatisfied(items, age)) {
                return false;
            }
        }
        return true;
    }

    private String getProductInSaleListJSONFormat(Collection<ProductInSaleDTO> items){
        List<Map<String, Object>> productInSaleList = new ArrayList<>();
        for (ProductInSaleDTO product : items) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("productId", product.getId());
            productMap.put("price",product.getPrice());
            productMap.put("quantity", product.getQuantity());
            productMap.put("category", Product.getCategoryStringFromInt(product.getCategory()));
            productInSaleList.add(productMap);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(productInSaleList);
        } catch (JsonProcessingException e) {
            logger.error("Error converting products to JSON", e);
            return "Error converting products to JSON";
        }
    }

    public String getPurchaseHistoryJSONFormat(){
        List<Purchase> purchases=salesHistory.getPurchases();
        List<Map<String, Object>> purchasesList = new ArrayList<>();
        for(Purchase purchase:purchases){
            Map<String, Object> purchaseMap = new HashMap<>();
            String productInSaleListJSON = getProductInSaleListJSONFormat(purchase.getProductInSaleList());
            purchaseMap.put("productInSaleList", productInSaleListJSON);
            purchaseMap.put("customUsername", purchase.getCustomerUsername());
            purchaseMap.put("totalPrice",purchase.getTotalPrice());
            purchaseMap.put("storeName", purchase.getStoreName());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(purchasesList);
        } catch (JsonProcessingException e) {
            logger.error("Error converting purchase history to JSON", e);
            return "Error converting purchase history to JSON";
        }
    }

    public String getPurchaseHistoryString(String username) {
        return salesHistory.getPurchaseHistory(username);
    }

    public void receiveMessage(String senderId, String senderUsername, String content){
        this.messages.add(new Message(senderId, senderUsername, content));
    }

    //region Discount creation
    public String getDiscountPoliciesInfo() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[ ");

        boolean firstElement = true;
        for (DiscountPolicy policy : discountPolicies) {
            if (!firstElement) {
                jsonBuilder.append(", ");
            }
            jsonBuilder.append(policy.getInfo());
            firstElement = false;
        }

        jsonBuilder.append(" ]");
        return jsonBuilder.toString();
    }

    public String getConditionInfo() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[ ");

        boolean firstElement = true;
        for (Condition condition : discountConditions) {
            if (!firstElement) {
                jsonBuilder.append(", ");
            }
            jsonBuilder.append(condition.getInfo());
            firstElement = false;
        }

        jsonBuilder.append(" ]");
        return jsonBuilder.toString();
    }

    public void addCategoryPercentageDiscount(int category, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 1)
            throw new IllegalArgumentException("Discount percent must be between 0 and 1");
        discountPolicies.add(new PercentageDiscountByCategory(category, discountPercent));
    }

    public void addProductPercentageDiscount(int productId, double discountPercent) {
        if (discountPercent < 0) throw new IllegalArgumentException("Discount percent must be between 0 and 1");
        discountPolicies.add(new PercentageDiscountByProduct(discountPercent, productId));
    }

    public void addStoreDiscount(double discountPercent) {
        if (discountPercent < 0) throw new IllegalArgumentException("Discount percent must be between 0 and 1");
        discountPolicies.add(new PercentageDiscountByStore(discountPercent));
    }

    public void addConditionalDiscount() {
        discountPolicies.add(new ConditionalDiscount());
    }

    public void addAdditiveDiscount() {
        discountPolicies.add(new AdditiveDiscount());
    }

    public void addMaxDiscount() {
        discountPolicies.add(new MaxDiscount());
    }

    public void addCategoryCountCondition(int category, int count) {
        if (count < 0) throw new IllegalArgumentException("Category item count cannot be less than 0");
        discountConditions.add(new CategoryCountCondition(category, count));
    }

    public void addTotalSumCondition(double requiredSum) {
        if (requiredSum < 0) throw new IllegalArgumentException("required sum cannot be less than 0");
        discountConditions.add(new TotalSumCondition(requiredSum));
    }

    public void addProductCountCondition(int productId, int count) {
        if (count < 0) throw new IllegalArgumentException("Product count cannot be less than 0");
        discountConditions.add(new ProductCountCondition(productId, count));
    }

    public void addAndDiscount() {
        discountPolicies.add(new AndDiscount());
    }

    public void addOrDiscount() {
        discountPolicies.add(new OrDiscount());
    }

    public void addXorDiscount() {
        discountPolicies.add(new XorDiscount());
    }

    public void removeDiscount(int selectedIndex) {
        if (selectedIndex >= discountPolicies.size())
            discountConditions.remove(selectedIndex - discountPolicies.size());
        else discountPolicies.remove(selectedIndex);
    }
    //endregion

    //region Discount/Condition editing/manipulation
    public void setFirstDiscount(int selectedDiscountIndex, int selectedFirstIndex) {
        if (selectedDiscountIndex == selectedFirstIndex)
            throw new IllegalArgumentException("Indexes cannot be the same");
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        DiscountPolicy setDiscount = discountPolicies.remove(selectedFirstIndex);
        editedDiscount.setFirst(setDiscount);
    }

    public void setSecondDiscount(int selectedDiscountIndex, int selectedSecondIndex) {
        if (selectedDiscountIndex == selectedSecondIndex)
            throw new IllegalArgumentException("Indexes cannot be the same");
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        DiscountPolicy setDiscount = discountPolicies.remove(selectedSecondIndex);
        editedDiscount.setSecond(setDiscount);
    }

    public void setFirstCondition(int selectedDiscountIndex, int selectedSecondIndex) {
        if (selectedDiscountIndex == selectedSecondIndex)
            throw new IllegalArgumentException("Indexes cannot be the same");
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        Condition setDiscount;
        if (selectedSecondIndex >= discountPolicies.size())
            setDiscount = discountConditions.remove(selectedSecondIndex - discountPolicies.size());
        else {
            if (selectedDiscountIndex < selectedSecondIndex) selectedSecondIndex -= 1;
            setDiscount = discountPolicies.remove(selectedSecondIndex);
        }
        editedDiscount.setFirst(setDiscount);
    }

    public void setSecondCondition(int selectedDiscountIndex, int selectedSecondIndex) {
        if (selectedDiscountIndex == selectedSecondIndex)
            throw new IllegalArgumentException("Indexes cannot be the same");
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        Condition setDiscount;
        if (selectedSecondIndex >= discountPolicies.size())
            setDiscount = discountConditions.remove(selectedSecondIndex - discountPolicies.size());
        else {
            if (selectedDiscountIndex < selectedSecondIndex) selectedSecondIndex -= 1;
            setDiscount = discountPolicies.remove(selectedSecondIndex);
        }
        editedDiscount.setSecond(setDiscount);
    }

    public void setThenDiscount(int selectedDiscountIndex, int selectedThenIndex) {
        if (selectedDiscountIndex == selectedThenIndex)
            throw new IllegalArgumentException("Indexes cannot be the same");
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        DiscountPolicy setDiscount = discountPolicies.remove(selectedThenIndex);
        editedDiscount.setThen(setDiscount);
    }

    public void setCategoryDiscount(int selectedDiscountIndex, int category) {
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        editedDiscount.setCategory(category);
    }

    public void setProductIdDiscount(int selectedDiscountIndex, int productId) {
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        editedDiscount.setProductId(productId);
    }

    public void setPercentDiscount(int selectedDiscountIndex, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 1)
            throw new IllegalArgumentException("Discount percent must be between 0 and 1");
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        editedDiscount.setPercent(discountPercent);
    }

    public void setDeciderDiscount(int selectedDiscountIndex, int selectedDeciderIndex) {
        if (selectedDiscountIndex == selectedDeciderIndex)
            throw new IllegalArgumentException("Indexes cannot be the same");
        DiscountPolicy editedDiscount = discountPolicies.get(selectedDiscountIndex);
        Condition setDiscount;
        if (selectedDeciderIndex >= discountPolicies.size())
            setDiscount = discountConditions.remove(selectedDeciderIndex - discountPolicies.size());
        else {
            if (selectedDiscountIndex < selectedDeciderIndex) selectedDeciderIndex -= 1;
            setDiscount = discountPolicies.remove(selectedDeciderIndex);
        }
        editedDiscount.setDecider(setDiscount);
    }

    public void setTotalSum(int selectedConditionIndex, double newSum) {
        Condition setCondition = discountConditions.get(selectedConditionIndex);
        setCondition.setSum(newSum);
    }

    public void setCountCondition(int selectedConditionIndex, int newCount) {
        Condition setCondition = discountConditions.get(selectedConditionIndex);
        setCondition.setCount(newCount);
    }

    public void setCategoryCondition(int selectedConditionIndex, int newCategory) {
        Condition setCondition = discountConditions.get(selectedConditionIndex);
        setCondition.setCategory(newCategory);
    }
    //endregion

    //region Policy creation
    public String getPurchasePoliciesInfo() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[ ");

        boolean firstElement = true;
        for (PurchasePolicy policy : purchasePolicies) {
            if (!firstElement) {
                jsonBuilder.append(", ");
            }
            jsonBuilder.append(policy.getPurchasePolicyInfo());
            firstElement = false;
        }

        jsonBuilder.append(" ]");
        return jsonBuilder.toString();
    }

    public void addPurchasePolicyByAge(int ageToCheck, int category) {
        if (ageToCheck <= 0)
            throw new IllegalArgumentException("Parameter " + ageToCheck + " cannot be negative or zero");
        if (category <= 0) throw new IllegalArgumentException("Parameter " + category + " cannot be negative or zero");
        purchasePolicies.add(new PurchasePolicyByAge(ageToCheck, category));
    }

    public void addPurchasePolicyByCategoryAndDate(int category, LocalDateTime dateTime) {
        if (category <= 0) throw new IllegalArgumentException("Parameter " + category + " cannot be negative or zero");
        if (dateTime == null) throw new IllegalArgumentException("Parameter " + dateTime + " cannot be null");
        purchasePolicies.add(new PurchasePolicyByCategoryAndDate(category, dateTime));
    }

    public void addPurchasePolicyByDate(LocalDateTime dateTime) {
        if (dateTime == null) throw new IllegalArgumentException("Parameter " + dateTime + " cannot be null");
        purchasePolicies.add(new PurchasePolicyByDate(dateTime));
    }

    public void addPurchasePolicyByProductAndDate(int productId, LocalDateTime dateTime) {
        if (productId < 0) throw new IllegalArgumentException("Parameter " + productId + " cannot be negative");
        if (dateTime == null) throw new IllegalArgumentException("Parameter " + dateTime + " cannot be null");
        purchasePolicies.add(new PurchasePolicyByProductAndDate(productId, dateTime));
    }

    public void addPurchasePolicyByShoppingCartMaxProductsUnit(int productId, int numOfQuantity) {
        if (productId < 0) throw new IllegalArgumentException("Parameter " + productId + " cannot be negative");
        if (numOfQuantity <= 0)
            throw new IllegalArgumentException("Parameter " + numOfQuantity + " cannot be negative and equal");
        purchasePolicies.add(new PurchasePolicyByShoppingCartMaxProductsUnit(productId, numOfQuantity));
    }

    public void addPurchasePolicyByShoppingCartMinProducts(int numOfQuantity) {
        if (numOfQuantity <= 0)
            throw new IllegalArgumentException("Parameter " + numOfQuantity + " cannot be negative and equal");
        purchasePolicies.add(new PurchasePolicyByShoppingCartMinProducts(numOfQuantity));
    }

    public void addPurchasePolicyByShoppingCartMinProductsUnit(int productId, int numOfQuantity) {
        if (productId < 0) throw new IllegalArgumentException("Parameter " + productId + " cannot be negative");
        if (numOfQuantity <= 0)
            throw new IllegalArgumentException("Parameter " + numOfQuantity + " cannot be negative and equal");
        purchasePolicies.add(new PurchasePolicyByShoppingCartMinProductsUnit(productId, numOfQuantity));
    }

    public void addAndPurchasePolicy() {
        purchasePolicies.add(new AndPolicy());
    }

    public void addOrPurchasePolicy() {
        purchasePolicies.add(new OrPolicy());
    }

    public void addConditioningPurchasePolicy() {
        purchasePolicies.add(new ConditioningPolicy());
    }

    public void setPurchasePolicyProductId(int selectedIndex, int productId) {
        PurchasePolicy purchasePolicy = purchasePolicies.get(selectedIndex);
        purchasePolicy.setPurchasePolicyProduct(productId);
    }

    public void setPurchasePolicyNumOfQuantity(int selectedIndex, int numOfQuantity) {
        PurchasePolicy purchasePolicy = purchasePolicies.get(selectedIndex);
        purchasePolicy.setPurchasePolicyNumOfQuantity(numOfQuantity);
    }

    public void setPurchasePolicyDateTime(int selectedIndex, LocalDateTime dateTime) {
        PurchasePolicy purchasePolicy = purchasePolicies.get(selectedIndex);
        purchasePolicy.setPurchasePolicyDateTime(dateTime);
    }

    public void setPurchasePolicyAge(int selectedIndex, int age) {
        PurchasePolicy purchasePolicy = purchasePolicies.get(selectedIndex);
        purchasePolicy.setPurchasePolicyAge(age);
    }

    public void setFirstPurchasePolicy(int selectedDiscountIndex, int selectedFirstIndex) {
        if (selectedDiscountIndex == selectedFirstIndex)
            throw new IllegalArgumentException("Indexes cannot be the same");
        PurchasePolicy editedDiscount = purchasePolicies.get(selectedDiscountIndex);
        PurchasePolicy setDiscount = purchasePolicies.remove(selectedFirstIndex);
        editedDiscount.setPurchasePolicyFirst(setDiscount);
    }

    public void setSecondPurchasePolicy(int selectedDiscountIndex, int selectedSecondIndex) {
        if (selectedDiscountIndex == selectedSecondIndex)
            throw new IllegalArgumentException("Indexes cannot be the same");
        PurchasePolicy editedDiscount = purchasePolicies.get(selectedDiscountIndex);
        PurchasePolicy setDiscount = purchasePolicies.remove(selectedSecondIndex);
        editedDiscount.setPurchasePolicySecond(setDiscount);
    }

    public void removePurchasePolicy(int selectedIndex) {
        if (selectedIndex >= purchasePolicies.size()) purchasePolicies.remove(selectedIndex - purchasePolicies.size());
        else purchasePolicies.remove(selectedIndex);
    }

    public void placeBid(String userName, int productID, double price)
    {
        Bid newBid = new Bid(userName, productID, price);
        this.bids.add(newBid);
    }

    public boolean approveBid(String userName, int productID, String bidUserName)
    {
        for ( Bid b : bids )
        {
            if(b.getProductID() == productID && b.getUserName().equals(bidUserName))
            {
                b.approveBid(userName);
                if(b.getApprovedBy().containsAll(owners))
                    b.setAllOwnersApproved(true);
                return b.getAllOwnersApproved();
            }
        }
        return false;
    }

    public void rejectBid(String userName, int productID, String bidUserName)
    {
        for ( Bid b : bids )
        {
            if(b.getProductID() == productID && b.getUserName().equals(bidUserName))
            {
                bids.remove(b);
                break;
            }
        }
    }

    public void counterOffer(String userName, int productID, String bidUserName, double newPrice)
    {
        for ( Bid b : bids )
        {
            if(b.getProductID() == productID && b.getUserName().equals(bidUserName))
            {
                b.setPrice(newPrice);
            }
        }

        //TODO: send notification
    }

    public boolean isBidExist(int productID, String bidUserName) {

        for ( Bid b : bids)
        {
            if(b.getProductID() == productID && b.getUserName().equals(bidUserName))
                return true;
        }
        return false;
    }

    public String getStoreBids() {
        if(bids.isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"storeName\" : \"").append(nameId).append("\",\n");
        sb.append("  \"bids\" : ").append(Bid.toJsonList(bids)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    public boolean isBidApproved(String username, int productId, double price) {
        for ( Bid b : bids)
        {
            if(b.getProductID() == productId && b.getUserName().equals(username) && b.getPrice() == price)
            {
                if(b.getApprovedBy().containsAll(owners))
                     return true;
            }
        }
        return false;
    }

    public String getMyBids(String userName) {
        LinkedList<Bid> filteredBids = bids.stream()
                .filter(bid -> userName.equals(bid.getUserName()))
                .collect(Collectors.toCollection(LinkedList::new));
        if(filteredBids.isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"storeName\" : \"").append(nameId).append("\",\n");
        sb.append("  \"bids\" : ").append(Bid.toJsonList(filteredBids)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    public void createProductLottery(int productID, LocalDateTime localDateTime, double price){
        ProductLottery productLottery = new ProductLottery(localDateTime, price);
        this.lotteryProducts.put(productID, productLottery);
    }

    public boolean buyLotteryProductTicket(String userName, int productID, double price) throws Exception{
        return lotteryProducts.get(productID).buyLotteryProductTicket(userName, price);
    }

    public String makeLotteryOnProduct(int productID)
    {
        return lotteryProducts.get(productID).makeLotteryOnProduct();
    }

    public boolean isLotteryExist(int productID) {
        return lotteryProducts.containsKey(productID);
    }

    public void editProduct(int productId, String productName, String productDescription, double productPrice, int productQuantity) {
        Product product = getProduct(productId);
        synchronized (product) {
            if (product != null) {
                product.editProduct(productName, productDescription, productPrice, productQuantity);
            } else throw new IllegalArgumentException("Product with id " + productId + " does not exist");
        }

    }


    public double getBidPrice(String userName, int productID) {
        for ( Bid b : bids)
        {
            if(b.getProductID() == productID && b.getUserName().equals(userName))
            {
                return b.getPrice();
            }
        }
        return 0;
    }

    public void removeBidAccepted(String userName, int productID) {
        for ( Bid b : bids)
        {
            if(b.getProductID() == productID && b.getUserName().equals(userName))
            {
                bids.remove(b);
                break;
            }
        }
    }

    public  List<Product> searchProduct(String keyWord, double minPrice, double maxPrice, List<Integer> intCategories, Double rating) {

        List<Product> productList = new ArrayList<>(products.values());

        // Stream the list and apply filters
        return productList.stream()
                .filter(product -> {
                    if (keyWord != null && !keyWord.isEmpty()) {
                        return product.getKeyWords().contains(keyWord);
                    }
                    return true;
                })
                .filter(product -> {
                    if (minPrice != 0) {
                        return product.getProduct_price() >= minPrice;
                    }
                    return true;
                })
                .filter(product -> {
                    if (maxPrice !=0){
                        return product.getProduct_price() <= maxPrice;
                    }
                    return true;
                })
                .filter(product -> {
                    if (intCategories != null && !intCategories.isEmpty()) {
                        return intCategories.contains(product.getCategory().getIntValue());
                    }
                    return true;
                })
                .filter(product -> {
                        return product.getRating() == rating;
                })
                .collect(Collectors.toList());
    }

    //endregion

}
