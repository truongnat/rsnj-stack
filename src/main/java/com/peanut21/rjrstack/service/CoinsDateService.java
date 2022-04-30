package com.peanut21.rjrstack.service;

import com.peanut21.rjrstack.model.*;
import com.peanut21.rjrstack.utils.HttpUtils;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.GetArgs;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import io.github.dengliming.redismodule.redistimeseries.DuplicatePolicy;
import io.github.dengliming.redismodule.redistimeseries.RedisTimeSeries;
import io.github.dengliming.redismodule.redistimeseries.Sample;
import io.github.dengliming.redismodule.redistimeseries.TimeSeriesOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CoinsDateService {
    public static final String GET_COINS_API = "https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=24h&tiers%5B0%5D=1&orderBy=marketCap&orderDirection=desc&limit=50&offset=0";
    public static final String GET_COIN_HISTORY_API = "https://coinranking1.p.rapidapi.com/coin/";
    public static final String COIN_HISTORY_TIME_PERIOD_PARAM = "/history?timePeriod=";
    public static final List<String> timePeriods = List.of("24h", "7d", "30d", "3m", "1y", "3y", "5y");

    public static final String REDIS_KEY_COINS = "coins";
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    RedisJSON redisJSON;

    @Autowired
    private RedisTimeSeries redisTimeSeries;

    public List<CoinInfo> fetchAllCoinsFromRedisJSON() {
        return getAllCoinsFromRedisJSON();
    }

    public void fetchCoins() {
        log.info("Inside getCoins()");
        ResponseEntity<Coins> coinsEntity = restTemplate.exchange(GET_COINS_API, HttpMethod.GET, HttpUtils.getHttpEntity(), Coins.class);
        storeCoinsToRedisJSON(coinsEntity.getBody());
    }

    public void fetchCoinHistory() {
        log.info("Inside fetchCoinHistory()");
        List<CoinInfo> allCoins = getAllCoinsFromRedisJSON();
        allCoins.forEach(coinInfo -> {
            timePeriods.forEach(s -> {
                fetchCoinHistoryForTimePeriod(coinInfo, s);
            });
        });
    }

    private void fetchCoinHistoryForTimePeriod(CoinInfo coinInfo, String timePeriod) {
        log.info("Fetching Coin History of {} for Time Period {}", coinInfo.getName(), timePeriod);
        String url = GET_COIN_HISTORY_API + coinInfo.getUuid() + COIN_HISTORY_TIME_PERIOD_PARAM + timePeriod;
        ResponseEntity<CoinPriceHistory> coinPriceHistoryResponseEntity = restTemplate.exchange(url, HttpMethod.GET, HttpUtils.getHttpEntity(), CoinPriceHistory.class);
        log.info("Data Fetched From API for coin History of {} for Time Period {}", coinPriceHistoryResponseEntity.getBody(), timePeriod);

        storeCoinHistoryToRedisTS(coinPriceHistoryResponseEntity.getBody(), coinInfo.getSymbol(), timePeriod);
    }

    private void storeCoinHistoryToRedisTS(CoinPriceHistory coinPriceHistory, String symbol, String timePeriod) {
        log.info("Data Fetched From API for coin History of {} for Time Period {}", symbol, timePeriod);

        List<CoinPriceHistoryExchangeRate> coinExchangeRate = coinPriceHistory.getData().getHistory();
        //        Symbol: timePeriod
        //        BTC: 24h. BTC: 1y, ETH: 3y
        coinExchangeRate.stream().filter(ch -> ch.getPrice() != null && ch.getTimestamp() != null).forEach(ch -> {
            redisTimeSeries.add(new Sample(symbol + ":" + timePeriod, Sample.Value.of(Long.parseLong(ch.getTimestamp()),
                    Double.parseDouble(ch.getPrice()))), new TimeSeriesOptions()
                    .unCompressed()
                    .duplicatePolicy(DuplicatePolicy.LAST));
        });
        log.info("Complete: Stored Coin History of {} for Time Period {} into Redis TS", symbol, timePeriod);
    }

    private List<CoinInfo> getAllCoinsFromRedisJSON() {
        CoinData coinData = redisJSON.get(REDIS_KEY_COINS, CoinData.class, new GetArgs().path(".data").indent("\t").newLine("\n").space(" "));
        return coinData.getCoins();
    }

    private void storeCoinsToRedisJSON(Coins coins) {
        redisJSON.set(REDIS_KEY_COINS, SetArgs.Builder.create(".", GsonUtils.toJson(coins)));
    }

    public List<Sample.Value> fetchCoinHistoryTimePeriodFromRedisTS(String symbol, String timePeriod) {
        Map<String,Object> tsInfo = fetchTsInfoForSymbol(symbol,timePeriod);
        Long firstTimeStamp = Long.valueOf(tsInfo.get("firstTimestamp").toString());
        Long lastTimestamp = Long.valueOf(tsInfo.get("lastTimestamp").toString());

        List<Sample.Value> coinsTSData = fetchTSDataForCoin(symbol,timePeriod,firstTimeStamp,lastTimestamp);
        return coinsTSData;
    }

    private List<Sample.Value> fetchTSDataForCoin(String symbol, String timePeriod, Long firstTimeStamp, Long lastTimestamp) {
        String key = symbol + ":" + timePeriod;
       return redisTimeSeries.range(key,firstTimeStamp,lastTimestamp);
    }

    private Map<String,Object> fetchTsInfoForSymbol(String symbol, String timePeriod) {
        return redisTimeSeries.info(symbol + ":" + timePeriod);
    }
}
