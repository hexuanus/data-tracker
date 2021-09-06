package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.MarketPrice;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class PriceClient {
    private static final ObjectMapper SAFE_MAPPER = new ObjectMapper();
    private CloseableHttpClient httpClient;
    private String url = "https://api.cryptowat.ch";
    private String path = "/markets/prices";

    public PriceClient() {
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClient = httpClientBuilder.build();
    }

    /**
     * Get a list of {@link MarketPrice} from https://api.cryptowat.ch/markets/prices
     *
     * @return list of {@link MarketPrice}
     */
    public List<MarketPrice> getPrices() throws IOException {
        long unixTime = System.currentTimeMillis();
        String uri = UriBuilder.fromPath(url).path(path).build().toString();
        Map<String, String> headers = getRequestHeaders();
        Map<String, String> entityMap = getEntity(headers, uri);

        List<MarketPrice> marketPriceList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entityMap.entrySet()) {
            MarketPrice marketPrice =
                    new MarketPrice(entry.getKey(), Double.valueOf(entry.getValue()), unixTime);
            marketPriceList.add(marketPrice);
        }

        return marketPriceList;
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json; charset=UTF-8");
        headers.put("content-type", "application/json");
        return headers;
    }

    private Map<String, String> getEntity(Map<String, String> headers, String uri)
            throws IOException {
        Map<String, String> entity = new HashMap<>();
        CloseableHttpResponse res = this.getWithHeaders(uri, headers);
        int statusCode = res.getStatusLine().getStatusCode();
        String entityStr = EntityUtils.toString(res.getEntity(), "UTF-8");
        res.close();
        if (statusCode == 200) {
            if (!StringUtils.isEmpty(entityStr)) {
                entity = SAFE_MAPPER.readValue(entityStr, new TypeReference<>() {});
            }
        }

        return entity;
    }

    private CloseableHttpResponse getWithHeaders(String uri, Map<String, String> headers)
            throws IOException {
        HttpGet request = new HttpGet(uri);
        request.setHeaders(
                headers
                        .entrySet()
                        .stream()
                        .map(e -> new BasicHeader(e.getKey(), e.getValue()))
                        .toArray(BasicHeader[]::new));
        return httpClient.execute(request);
    }
}