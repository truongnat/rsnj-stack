package com.peanut21.rjrstack.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoinPriceHistoryData {
    private String change;
    private List<CoinPriceHistoryExchangeRate> history = new ArrayList<>();
}
