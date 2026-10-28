package com.inventar.inventar.product.repository;

import com.inventar.inventar.product.model.ProductMovementDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductMovementDateRepository extends JpaRepository<ProductMovementDate, Long> {
    List<ProductMovementDate> findByProductBarcodeOrderByEntryDateDesc(String barcode);
}
