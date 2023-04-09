package com.spuntik.playground.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDetails {

    private BigDecimal amount;

    private Integer itemId;



}
