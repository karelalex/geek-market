package com.geekbrains.geekmarketwinter.repositories;

import com.geekbrains.geekmarketwinter.entites.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
    Product findOneByTitle(String title);
    Optional<Product> findOneByVendorCode(String vendorCode);

    @Query(value = "select  count(*) from orders_item where product_id=:id", nativeQuery = true)
    Integer getOrgerCountOfProduct(@Param("id") Long id);
}
