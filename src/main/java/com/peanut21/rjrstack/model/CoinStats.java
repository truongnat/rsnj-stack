package com.peanut21.rjrstack.model;


import lombok.Data;

@Data
public class CoinStats {
    private Float total;
    private Float totalCoins;
    private Float totalMarkets;
    private Float totalExchanges;
    private String totalMarketCap;
    private String total24hVolume;
}
