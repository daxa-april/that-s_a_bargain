package com.step.bargain.MarketplaceParser;

import com.step.bargain.model.entity.Platform;
import com.step.bargain.model.entity.dto.ProductInfo;
import org.hibernate.query.sqm.ParsingException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daria Pevets
 **/
@Component
public class WildberriesParser implements MarketplaceParser {

    private final RestClient restClient = RestClient.builder()
            .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pattern ARTICLE_PATTERN = Pattern.compile("catalog/(\\d+)");


    private String extractArticle(String url) {

        Matcher matcher = ARTICLE_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Некорректная ссылка на WB");
    }

    @Override
    public Platform getPlatform() {
        return Platform.WILDBERRIES;
    }

    @Override
    public ProductInfo parse(String url) {

        String article = extractArticle(url);

        String apiUrl = "https://www.wildberries.by/__internal/u-card/cards/v4/detail?appType=1&curr=byn&dest=-59208&spp=30&hide_vflags=4294967296&hide_ndflags=294912&hide_dtype=11%3B13%3B15&mtype=257&lang=ru&ab_testing=false&nm=" + article;

        try {
            String jsonResponse = restClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode products = root.path("products");

            if (!products.isArray() || products.isEmpty()) {
                throw new ParsingException("Товар не найден на WB по артикулу: " + article);
            }

            JsonNode product = products.get(0);

            String brand = product.path("brand").asText("");
            String name = product.path("name").asText("Без названия");
            String title = brand.isEmpty() ? name : brand + " / " + name;

            int totalQuantity = product.path("totalQuantity").asInt(0);
            boolean inStock = totalQuantity > 0;

            BigDecimal currentPrice = BigDecimal.ZERO;
            JsonNode sizes = product.path("sizes");
            if (sizes.isArray() && !sizes.isEmpty()) {
                long priceInKopecks = sizes.get(0).path("price").path("product").asLong(0);
                currentPrice = BigDecimal.valueOf(priceInKopecks)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }

            return new ProductInfo(title, currentPrice, "BYN", inStock);

        } catch (Exception e) {
            throw new ParsingException("Ошибка при парсинге WB: " + url);
        }
    }
}
