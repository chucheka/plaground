package com.spuntik.playground.services;

import java.math.BigDecimal;

public class Testing {
    public void testing(){

        double d = 1.1;

        BigDecimal bd1 = new BigDecimal(d); // Noncompliant; see comment above
        BigDecimal bd2 = new BigDecimal(1.1); // Noncompliant; same result

        for (int i = 10; i < 10; i++) {
            System.out.println("THIS CODE WILL NVER EXECUTE");
        }

    }
}
