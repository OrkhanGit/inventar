package com.inventar.inventar.product.repository;

import com.inventar.inventar.enums.Status;
import com.inventar.inventar.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByStatusIs(Status status);
    Optional<Product> findByBarcodeAndStatus(String barcode, Status status);

    @Query("SELECT p FROM Product p WHERE p.barcode IN :barcodes AND p.status = :status")
    List<Product> findByBarcodeInAndStatus(@Param("barcodes") List<String> barcodes, @Param("status") Status status);

    List<Product> findByLocation(String location);

    List<Product> findByBarcodeIn(List<String> barcodes);

    Optional<Product> findByBarcode(String barcode);
}
