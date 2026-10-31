package com.inventar.inventar.product.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductServiceDto {

    private String barcode;
    private String name;
    private Long count;
    private String fromUser;
    private LocalDateTime entryDate;
    private String description;
    private Long serviceCount = 0L;

}
