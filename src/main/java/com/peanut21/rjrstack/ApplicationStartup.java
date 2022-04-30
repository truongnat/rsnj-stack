package com.peanut21.rjrstack;

import com.peanut21.rjrstack.service.CoinsDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private CoinsDateService coinsDateService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
//        coinsDateService.fetchCoins();
//        coinsDateService.fetchCoinHistory();
    }
}
