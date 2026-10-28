package com.inventar.inventar.product.controller;

import com.inventar.inventar.product.model.ProductDto;
import com.inventar.inventar.product.model.ProductServiceDto;
import com.inventar.inventar.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/createProduct")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {

        return ResponseEntity.ok().body(productService.createProduct(productDto));

    }

    @GetMapping("/allProducts")
    public ResponseEntity<List<ProductDto>> getAllProducts() {

        return ResponseEntity.ok().body(productService.getAllProducts());

    }

    @GetMapping("/service")
    public ResponseEntity<List<ProductServiceDto>> getSkanerProducts(@RequestParam String service) {

        return ResponseEntity.ok().body(productService.getSkanerProducts(service));

    }

    @GetMapping("/entryDates")
    public ResponseEntity<List<LocalDateTime>> getEntryDates(@RequestParam String barcode) {
        return ResponseEntity.ok(productService.getEntryDates(barcode));
    }

    @GetMapping("/byBarcode")
    public ResponseEntity<ProductDto> getByBarcode(@RequestParam String barcode) {
        ProductDto dto = productService.getByBarcode(barcode);
        if (dto == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(dto);
    }


}
