package com.example.trading_system.domain.stores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepositoryData extends JpaRepository<Store, String> {

    // Find a store by its name
    Store findByNameId(String nameId);

    // Find all stores that are active
    List<Store> findByIsActiveTrue();

    // Find all stores that are open
    List<Store> findByIsOpenTrue();

    // Find stores by a specific owner's username
    @Query("SELECT s FROM Store s JOIN s.owners o WHERE o = :owner")
    List<Store> findByOwner(@Param("owner") String owner);

    // Find stores by a specific manager's username
    @Query("SELECT s FROM Store s JOIN s.managers m WHERE m = :manager")
    List<Store> findByManager(@Param("manager") String manager);

    // Find stores by their founder's username
    List<Store> findByFounder(String founder);

    // Find stores by a specific rating
    List<Store> findByStoreRating(Double storeRating);

    // Find stores with a minimum rating
    @Query("SELECT s FROM Store s WHERE s.storeRating >= :rating")
    List<Store> findByStoreRatingGreaterThanEqual(@Param("rating") Double rating);

    // Find stores with a maximum rating
    @Query("SELECT s FROM Store s WHERE s.storeRating <= :rating")
    List<Store> findByStoreRatingLessThanEqual(@Param("rating") Double rating);

    // Custom query to search stores by partial name match
    @Query("SELECT s FROM Store s WHERE s.nameId LIKE %:name%")
    List<Store> findByNameContaining(@Param("name") String name);

    // Find all stores with a specific product
    @Query("SELECT s FROM Store s JOIN s.products p WHERE p.product_id= :productId")
    List<Store> findByProductId(@Param("productId") int productId);
}
