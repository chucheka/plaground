package com.spuntik.playground.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Item {

    private Long id;

    private String name;

    private String category;

    private String description;

    private BigDecimal amount;
}
