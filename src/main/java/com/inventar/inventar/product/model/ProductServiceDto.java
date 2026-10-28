package com.inventar.inventar.product.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductServiceDto {

    private String barcode;
    private String name;
    private Long count;
    private String owner;
    private LocalDateTime entryDate;
    private String description;

}
