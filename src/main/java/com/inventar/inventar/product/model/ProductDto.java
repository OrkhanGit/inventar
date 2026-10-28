package com.inventar.inventar.product.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductDto {

    private String barcode;
    private String name;
    private String toLocation;
    private String location;
    private String fromUser;
    private LocalDateTime entryDate;
    private String description;
    private Long count;

}
