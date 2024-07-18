package com.example.trading_system.PersistenceTests;

import com.example.trading_system.TradingSystemRestController;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.stores.discountPolicies.Condition;
import com.example.trading_system.domain.stores.discountPolicies.DiscountPolicy;
import com.example.trading_system.domain.stores.purchasePolicies.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:applicationTests.properties")
@Transactional
@Disabled
public class StorePersistentTests { //TESTS TO BE RUN MANUALLY WITH A DB CONNECTION
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ApplicationContext applicationContext;

    private StoreRepository storeRepository;

    private StoreDatabaseRepository anotherStoreRepository;

    @BeforeEach
    void setUp() {
        applicationContext.getBean(TradingSystemRestController.class).deleteData();
        storeRepository = applicationContext.getBean(StoreDatabaseRepository.class);
    }

    @AfterEach
    void tearDown() {
        storeRepository.deleteData();
    }

    private void initNewRepo() {
        anotherStoreRepository = new StoreDatabaseRepository();
        anotherStoreRepository.setEntityManager(entityManager);
    }

    @Test
    void createStoreAndRetrieve() {
        Store store=new Store("Adidas","Sport store","testUser",4.0);
        storeRepository.save(store);
        initNewRepo();
        Store retrievedStore=anotherStoreRepository.getStore("Adidas");
        assertNotNull(retrievedStore);
        assertEquals("Adidas", retrievedStore.getNameId());
        assertEquals("Sport store", retrievedStore.getDescription());
        assertEquals("testUser", retrievedStore.getFounder());
        assertEquals(4.0, retrievedStore.getStoreRating());
    }

    @Test
    void createPurchaseAndRetrieve() {
        ProductInSaleDTO product=new ProductInSaleDTO("Adidas",123,100.0,1,1);
        List<ProductInSaleDTO> list=new ArrayList<>();
        list.add(product);
        Purchase purchase=new Purchase("testUser",list,100.0,"Adidas");
        Store store=new Store("Adidas","Sport store","testUser",4.0);
        store.addPurchase(purchase);
        storeRepository.save(store);
        initNewRepo();
        List<Purchase> purchases=anotherStoreRepository.getStore("Adidas").getSalesHistory().getPurchases();
        Purchase retrievePurchase=purchases.get(0);
        assertNotNull(retrievePurchase);
        assertEquals("testUser",retrievePurchase.getCustomerUsername());
        assertEquals("Adidas",retrievePurchase.getProductInSaleList().get(0).getStoreId());
        assertEquals(123,retrievePurchase.getProductInSaleList().get(0).getId());
        assertEquals(100.0,retrievePurchase.getProductInSaleList().get(0).getPrice());
        assertEquals(1,retrievePurchase.getProductInSaleList().get(0).getQuantity());
        assertEquals(1,retrievePurchase.getProductInSaleList().get(0).getCategory());
        assertEquals(100.0, retrievePurchase.getTotalPrice());
        assertEquals("Adidas", retrievePurchase.getStoreName());
    }

    @Test
    void createStoreSaleHistoryAndRetrieve() {
        ProductInSaleDTO product=new ProductInSaleDTO("Adidas",123,100.0,1,1);
        List<ProductInSaleDTO> list=new ArrayList<>();
        list.add(product);
        Purchase purchase=new Purchase("testUser",list,100.0,"Adidas");
        Store store=new Store("Adidas","Sport store","testUser",4.0);
        StoreSalesHistory salesHistory=store.getSalesHistory();
        salesHistory.addPurchase(purchase);
        storeRepository.save(store);
        initNewRepo();
        List<Purchase> purchases=anotherStoreRepository.getStore("Adidas").getSalesHistory().getPurchases();
        StoreSalesHistory retrieveStoreSalesHistory= anotherStoreRepository.getStore("Adidas").getSalesHistory();
        Purchase retrievePurchase=purchases.get(0);
        assertNotNull(retrieveStoreSalesHistory);
        assertEquals("testUser",retrievePurchase.getCustomerUsername());
        assertEquals("Adidas",retrievePurchase.getProductInSaleList().get(0).getStoreId());
        assertEquals(123,retrievePurchase.getProductInSaleList().get(0).getId());
        assertEquals(100.0,retrievePurchase.getProductInSaleList().get(0).getPrice());
        assertEquals(1,retrievePurchase.getProductInSaleList().get(0).getQuantity());
        assertEquals(1,retrievePurchase.getProductInSaleList().get(0).getCategory());
        assertEquals(100.0, retrievePurchase.getTotalPrice());
        assertEquals("Adidas", retrievePurchase.getStoreName());
        assertEquals(retrievePurchase,retrieveStoreSalesHistory.getAllPurchases().get(0));
    }

    @Disabled
    @Test
    void createProductAndRetrieve() {
        Product product=new Product(123,"Milk","Cow Milk",5.0,50,5.0,Category.Food,new ArrayList<>());
        Store store=new Store("Shufersal","Market store","testUser",4.0);
        store.addProduct(123,"Milk","Cow Milk",5.0,50,5.0,3,new ArrayList<>());
        storeRepository.save(store);
        initNewRepo();
        Store retrievedStore=anotherStoreRepository.getStore("Shufersal");
        Product retrieveProduct=retrievedStore.getProduct(123);
        assertEquals(123,retrieveProduct.getProduct_id());
        assertEquals("Milk",retrieveProduct.getProduct_name());
        assertEquals("Cow Milk",retrieveProduct.getProduct_description());
        assertEquals(5.0,retrieveProduct.getProduct_price());
        assertEquals(50,retrieveProduct.getProduct_quantity());
        assertEquals(5.0,retrieveProduct.getRating());
        assertEquals(Category.Food,retrieveProduct.getCategory());
        assertEquals(new ArrayList<>(),retrieveProduct.getKeyWords());
    }

    @Disabled
    @Test
    void createBidAndRetrieve() {
        Store store=new Store("Shufersal","Market store","testUser",4.0);
        store.addProduct(123,"Milk","Cow Milk",5.0,50,5.0,3,new ArrayList<>());
        store.placeBid("testUser",123,5.0,"Hadaat 6","1","USD","4111111111111111","12","2025","Emily Johnson","123","123456789");
        storeRepository.save(store);
        initNewRepo();
        Store retrievedStore=anotherStoreRepository.getStore("Shufersal");
        Bid receiveBid=retrievedStore.getBid(123,"testUser");
        assertEquals("testUser",receiveBid.getUserName());
        assertEquals("Hadaat 6",receiveBid.getAddress());
        assertEquals(123,receiveBid.getProductID());
        assertEquals("1",receiveBid.getAmount());
        assertEquals("USD",receiveBid.getCurrency());
        assertEquals("4111111111111111",receiveBid.getCardNumber());
        assertEquals("12",receiveBid.getMonth());
        assertEquals("2025",receiveBid.getYear());
        assertEquals("Emily Johnson",receiveBid.getHolder());
        assertEquals("123",receiveBid.getCcv());
        assertEquals("123456789",receiveBid.getHolderId());
    }

      @Test
    void createPurchasePolicyAndRetrieve() {
        Store store=new Store("Shufersal","Market store","testUser",4.0);
        store.addPurchasePolicyByAge(18, 1);
        store.addPurchasePolicyByDate(LocalDateTime.now().minusDays(1));
        storeRepository.save(store);
        initNewRepo();
        Store retrievedStore=anotherStoreRepository.getStore("Shufersal");
        PurchasePolicy purchasePolicyByAge=anotherStoreRepository.getStore("Shufersal").getPurchasePolicies().get(0);
        PurchasePolicy purchasePolicyByDate=anotherStoreRepository.getStore("Shufersal").getPurchasePolicies().get(1);
        assertEquals(store.getPurchasePolicies().get(0),purchasePolicyByAge);
        assertEquals(store.getPurchasePolicies().get(1),purchasePolicyByDate);
    }

    @Test
    void createDiscountPolicyAndRetrieve() {
        Store store=new Store("Shufersal","Market store","testUser",4.0);
        store.addStoreDiscount(0.4);
        store.addTotalSumCondition(100);
        storeRepository.save(store);
        initNewRepo();
        Store retrievedStore=anotherStoreRepository.getStore("Shufersal");
        DiscountPolicy storeDiscount=anotherStoreRepository.getStore("Shufersal").getDiscountPolicies().get(0);
        Condition totalSumCondition=anotherStoreRepository.getStore("Shufersal").getDiscountConditions().get(0);
        assertEquals(store.getDiscountPolicies().get(0),storeDiscount);
        assertEquals(store.getDiscountConditions().get(0),totalSumCondition);
    }










}
