package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketPriceRanking implements Comparable<MarketPriceRanking> {
  @JsonProperty("name")
  private String name;

  @JsonProperty("rank")
  private String rank;

  @JsonProperty("average_price")
  private double averagePrice;

  @JsonProperty("unix_time")
  private long unixTime;

  @Override
  public int compareTo(@NotNull MarketPriceRanking o) {
    if (o == null) {
      return -1;
    }

    if (this.averagePrice >= o.averagePrice) {
      return -1;
    } else {
      return 1;
    }
  }
}
