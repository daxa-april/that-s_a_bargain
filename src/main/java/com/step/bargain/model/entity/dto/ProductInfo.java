package com.step.bargain.model.entity.dto;

import java.math.BigDecimal;

/**
 * @author Daria Pevets
 **/
public record ProductInfo(
        String title,
        BigDecimal price,
        String currency,
        boolean inStock
) {

}
