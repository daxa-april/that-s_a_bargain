package com.step.bargain.model.entity;

import java.util.Arrays;
import java.util.List;

/**
 * @author Daria Pevets
 **/
public enum Platform {
    WILDBERRIES("wildberries.by", "wildberries.ru"),
    OZON("ozon.by", "ozon.ru"),
    LETOILE("letu.ru", "letoile.by"),
    GOLDEN_APPLE("goldapple.by", "goldapple.ru");

    private final List<String> domains;

    Platform(String... domains) {
        this.domains = List.of(domains);
    }

    public static Platform fromUrl(String url) {
        return Arrays.stream(values())
                .filter(platform -> platform.domains.stream().anyMatch(url::contains))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Маркетплейс не поддерживается: " + url));
    }
}
