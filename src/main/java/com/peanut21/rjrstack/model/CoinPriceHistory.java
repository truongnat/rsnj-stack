package com.peanut21.rjrstack.model;

import lombok.Data;

@Data
public class CoinPriceHistory {
    private String status;
    private CoinPriceHistoryData data;
}
