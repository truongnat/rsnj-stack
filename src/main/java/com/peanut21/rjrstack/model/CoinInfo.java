package com.peanut21.rjrstack.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoinInfo {
    private String uuid;
    private String symbol;
    private String name;
    private String color;
    private String iconUrl;
    private String marketCap;
    private String price;
    private Float listedAt;
    private Float tier;
    private String change;
    private Float rank;
    private List<String> sparkline = new ArrayList<>();
    private Boolean lowVolume;
    private String coinRankingUrl;
    private String _24hVolume;
    private String btcPrice;
}
