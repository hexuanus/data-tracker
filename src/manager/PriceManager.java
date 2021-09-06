package manager;

import client.PriceClient;
import entity.MarketPrice;
import entity.MarketPriceRanking;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PriceManager {
  private static final int refreshIntervalMinute = 1;
  private final PriceClient priceClient;
  private Timer timer;
  private Map<String, List<MarketPrice>> cache;

  public PriceManager(PriceClient priceClient) {
    this.priceClient = priceClient;
    this.cache = new HashMap<>();
    this.timer = new Timer(true);
  }

  /** enable the scheduler job which call price client to get a list of price per minute */
  public void start() {
    timer.schedule(
        new TimerTask() {
          @SneakyThrows
          @Override
          public void run() {
            try {
              cleanUpCache();
              List<MarketPrice> priceList = priceClient.getPrices();
              addPrices(priceList);
            } catch (Exception e) {
              log.error("timer.schedule throws exception", e);
            }
          }
        },
        0,
        TimeUnit.MINUTES.toMillis(refreshIntervalMinute));
  }

  /** close the scheduler job */
  public void shutdown() {
    if (this.timer != null) {
      this.timer.cancel();
    }
  }

  /**
   * Add price to cache
   *
   * @param marketPriceList list of {@link MarketPrice}
   */
  public void addPrices(List<MarketPrice> marketPriceList) {
    for (MarketPrice marketPrice : marketPriceList) {
      String name = marketPrice.getName();
      List<MarketPrice> priceList = cache.getOrDefault(name, new ArrayList<>());
      priceList.add(marketPrice);
      cache.put(name, priceList);
    }
  }

  /**
   * get price for the last 24 hours per name
   *
   * @param name
   * @return list of {@link MarketPrice}
   */
  public List<MarketPrice> getPrice(String name) {
    List<MarketPrice> marketPriceList = new ArrayList<>();
    long currentTimeMillis = System.currentTimeMillis();
    long offset = 86400000L;
    if (this.cache.containsKey(name)) {
      List<MarketPrice> priceList = this.cache.get(name);
      for (int index = priceList.size() - 1; index >= 0; index--) {
        MarketPrice marketPrice = marketPriceList.get(index);
        if (marketPriceList.get(index).getUnixTime() > currentTimeMillis - offset) {
          marketPriceList.add(marketPrice);
        } else {
          break;
        }
      }
    }

    return marketPriceList;
  }

  /**
   * get market price ranking for the last 24 hours
   *
   * @return list of {@link MarketPriceRanking}
   */
  public List<MarketPriceRanking> getRanking() {
    List<MarketPriceRanking> marketPriceRankingList = new ArrayList<>();
    int total = this.cache.size();
    long currentMil = System.currentTimeMillis();
    for (String name : this.cache.keySet()) {
      List<MarketPrice> marketPriceList = this.getPrice(name);
      double priceTotal = 0;
      for (MarketPrice marketPrice : marketPriceList) {
        priceTotal += marketPrice.getPrice();
      }

      MarketPriceRanking marketPriceRanking = new MarketPriceRanking();
      marketPriceRanking.setName(name);
      marketPriceRanking.setAveragePrice(priceTotal / (24 * 60));
      marketPriceRanking.setUnixTime(currentMil);
      marketPriceRankingList.add(marketPriceRanking);
    }

    // sort based on average price of last 24 hours
    Collections.sort(marketPriceRankingList);
    // set ranking
    int ranking = 1;
    for (MarketPriceRanking marketPriceRanking : marketPriceRankingList) {
      marketPriceRanking.setRank(ranking + "/" + total);
      ranking++;
    }

    return marketPriceRankingList;
  }

  /** clean up cache after 24 hours ttl */
  private void cleanUpCache() {
    long offset = 86400000L;
    long currentMil = System.currentTimeMillis();
    for (String name : this.cache.keySet()) {
      List<MarketPrice> marketPriceList = new ArrayList<>();
      List<MarketPrice> list = this.cache.get(name);
      for (MarketPrice marketPrice : list) {
        if (currentMil <= marketPrice.getUnixTime() + offset) {
          marketPriceList.add(marketPrice);
        }
      }

      this.cache.put(name, marketPriceList);
    }
  }
}
