package com.inventar.inventar.sparepart.model;

import com.inventar.inventar.enums.StockOperation;
import lombok.Data;

@Data
public class RequestStock {

    private Long sparePartId;
    private Long amount;
    private StockOperation operation;

}
