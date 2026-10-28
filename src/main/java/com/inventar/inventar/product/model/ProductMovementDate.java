package com.inventar.inventar.product.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "product_movement_date")
public class ProductMovementDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime entryDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
