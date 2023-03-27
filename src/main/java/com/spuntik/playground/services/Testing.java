package com.spuntik.playground.services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Testing {
    public void testing(){

        for (int i = 1; i < 10; i++) {
            log.info("System out print not allowed by sonar qube");
        }

    }
}
