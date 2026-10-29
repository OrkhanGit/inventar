package com.inventar.inventar.act.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActDto {

    private String actNumber;
    private String location;
    private String toLocation;
    private List<String> barcode;
    private List<String> name;
    private List<Long> count;
    private LocalDateTime date;
    private String description;
    private String fromUser;
    private String toUser;
    private List<String> condition;
    private List<Boolean> toService;

}
