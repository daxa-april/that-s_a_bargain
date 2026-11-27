package com.step.bargain.MarketplaceParser;

import com.step.bargain.model.entity.Platform;
import com.step.bargain.model.entity.dto.ProductInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daria Pevets
 **/
@Component
public class OzonParser implements MarketplaceParser {


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
        throw new IllegalArgumentException("Некорректная ссылка на Ozon");
    }

    @Override
    public Platform getPlatform() {
        return Platform.OZON;
    }

    @Override
    public ProductInfo parse(String url) {

        String article = extractArticle(url);

        String apiUrl = "https://" + article;
    }//Todo: озон выделывается, порешать
}
