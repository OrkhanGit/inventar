package com.inventar.inventar.sparepart.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "spare_part")
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sparePartName;
    private Long count;



}
