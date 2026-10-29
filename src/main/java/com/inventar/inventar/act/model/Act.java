package com.inventar.inventar.act.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "act")
public class Act {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actNumber;
    private String toLocation;
    private String location;
    private List<String> barcode;
    private List<String> name;
    private List<Long> count;
    private LocalDateTime date;
    private String description;
    private String toUser;
    private String fromUser;
    private String owner;
    private List<String> condition;
    private List<Boolean> toService;


}
