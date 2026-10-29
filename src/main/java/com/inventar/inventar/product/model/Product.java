package com.inventar.inventar.product.model;

import com.inventar.inventar.enums.Condition;
import com.inventar.inventar.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String barcode;
    private String name;
    private String toLocation;
    private String location;
    private String fromUser;
    private Long count;
    private String description;
    private String owner;
    private Boolean toService = false;

    @Enumerated(EnumType.STRING)
    private Condition condition;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductMovementDate> movementsDate =  new ArrayList<>();

}
