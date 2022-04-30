package com.peanut21.rjrstack.controller;

import com.peanut21.rjrstack.model.CoinInfo;
import com.peanut21.rjrstack.model.HistoryData;
import com.peanut21.rjrstack.service.CoinsDateService;
import com.peanut21.rjrstack.utils.Utility;
import io.github.dengliming.redismodule.redistimeseries.Sample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/coins")
@Slf4j
public class CoinsRankingController {

    @Autowired
    private CoinsDateService coinsDateService;

    @GetMapping
    public ResponseEntity<List<CoinInfo>> fetchAllCoins() {
        return ResponseEntity.ok().body(coinsDateService.fetchAllCoinsFromRedisJSON());
    }


    @GetMapping("/{symbol}/{timePeriod}")
    public List<HistoryData> fetchCoinHistoryPerTimePeriod(
            @PathVariable String symbol,
            @PathVariable String timePeriod
    ) {
        List<Sample.Value> coinsTSData = coinsDateService.fetchCoinHistoryTimePeriodFromRedisTS(symbol, timePeriod);

        return coinsTSData.stream().map(value -> new HistoryData(Utility.convertUnixTimeToDate(value.getTimestamp()), Utility.round(value.getValue(), 2))).collect(Collectors.toList());
    }
}
