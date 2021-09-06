package resources;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import entity.MarketPrice;
import entity.MarketPriceRanking;
import manager.PriceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
@Api(
  value = "Endpoints for price data tracker related operations.",
  tags = {"Tickets"}
)

@Slf4j
public class PriceResource {
  private final PriceManager priceManager;

  public PriceResource(PriceManager priceManager) {
    this.priceManager = priceManager;
  }

  @GET
  @Path("/market_price")
  @Timed(name = "market_price_timer", absolute = true)
  @Metered(name = "market_price_meter", absolute = true)
  @ApiOperation(
    value = "get market price",
    notes = "get market price by given name.",
    response = MarketPrice.class,
    responseContainer = "List"
  )
  public Response getMarketPrice(@ApiParam(value = "name filter") @QueryParam("name") String name) {
    try {
      List<MarketPrice> marketPriceList = priceManager.getPrice(name);
      if (CollectionUtils.isNotEmpty(marketPriceList)) {
        return Response.ok(marketPriceList).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    } catch (Exception ex) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(new ErrorResponse(ex.toString()))
          .build();
    }
  }

  @GET
  @Path("/market_price_ranking")
  @Timed(name = "market_price_ranking_timer", absolute = true)
  @Metered(name = "market_price_ranking_meter", absolute = true)
  @ApiOperation(
    value = "get market price ranking",
    notes = "get market price ranking.",
    response = MarketPriceRanking.class,
    responseContainer = "List"
  )
  public Response getMarketPriceRanking() {
    try {
      List<MarketPriceRanking> marketPriceRankingList = priceManager.getRanking();
      if (CollectionUtils.isNotEmpty(marketPriceRankingList)) {
        return Response.ok(marketPriceRankingList).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    } catch (Exception ex) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(new ErrorResponse(ex.toString()))
          .build();
    }
  }
}
