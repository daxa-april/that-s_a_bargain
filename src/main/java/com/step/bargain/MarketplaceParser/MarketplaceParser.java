package com.step.bargain.MarketplaceParser;

import com.step.bargain.model.entity.Platform;
import com.step.bargain.model.entity.dto.ProductInfo;

/**
 * @author Daria Pevets
 **/
public interface MarketplaceParser {

    Platform getPlatform();

    ProductInfo parse(String url);
}
