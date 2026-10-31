package com.inventar.inventar.product.model;

import lombok.Data;

@Data
public class SearchProductDto {

    private String barcode;
    private String name;
    private String location;

}
