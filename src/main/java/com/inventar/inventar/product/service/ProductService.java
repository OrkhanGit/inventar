package com.inventar.inventar.product.service;

import com.inventar.inventar.enums.Status;
import com.inventar.inventar.mapper.AllMapper;
import com.inventar.inventar.product.model.Product;
import com.inventar.inventar.product.model.ProductDto;
import com.inventar.inventar.product.model.ProductMovementDate;
import com.inventar.inventar.product.model.ProductServiceDto;
import com.inventar.inventar.product.repository.ProductMovementDateRepository;
import com.inventar.inventar.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AllMapper productMapper;
    private final ProductMovementDateRepository productMovementDateRepository;

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {

        Product product = productMapper.toProduct(productDto);
        product.setStatus(Status.ACTIVE);

        ProductMovementDate movement = new ProductMovementDate();
        movement.setEntryDate(LocalDateTime.now());
        movement.setProduct(product);
        product.getMovementsDate().add(movement);

        return productMapper.toProductDto(productRepository.save(product));

    }

    public List<ProductDto> getAllProducts() {

        List<Product> allProducts = productRepository.findAllByStatusIs(Status.ACTIVE);
        return productMapper.toProductDto(allProducts);

    }


    public List<ProductServiceDto> getSkanerProducts(String service) {

        List<Product> product = productRepository.findByLocation(service);
        return productMapper.toProductServiceDto(product);

    }

    @Transactional(readOnly = true)
    public List<LocalDateTime> getEntryDates(String barcode) {
        return productMovementDateRepository.findByProductBarcodeOrderByEntryDateDesc(barcode)
                .stream()
                .map(ProductMovementDate::getEntryDate)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(productMapper::toProductDto)
                .orElse(null);
    }

}
