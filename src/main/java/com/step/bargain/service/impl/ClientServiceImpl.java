package com.step.bargain.service.impl;

import com.step.bargain.MarketplaceParser.MarketplaceParser;
import com.step.bargain.model.entity.Item;
import com.step.bargain.model.entity.Platform;
import com.step.bargain.model.entity.dto.ProductInfo;
import com.step.bargain.repository.ItemRepository;
import com.step.bargain.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Daria Pevets
 **/
@Service
public class ClientServiceImpl implements ClientService {

    private final ItemRepository itemRepository;

    private final Map<Platform, MarketplaceParser> parsers;

    public ClientServiceImpl(List<MarketplaceParser> parserList, ItemRepository itemRepository) {
        this.parsers = parserList.stream()
                .collect(Collectors.toMap(MarketplaceParser::getPlatform, Function.identity()));
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Item addItem(String url) {
        if (itemRepository.existsByUrl(url)) {
            throw new IllegalStateException("Товар по данной ссылке уже существует" + url);
        }

        Platform platform = Platform.fromUrl(url);

        MarketplaceParser parser = parsers.get(platform);
        if (parser == null) {
            throw new UnsupportedOperationException("Маркетплейс не поддерживается");
        }

        ProductInfo productInfo = parser.parse(url);

        Item item = new Item();

        item.setUrl(url);
        item.setTitle(productInfo.title());
        item.setPlatform(platform);
        item.setLastPrice(productInfo.price());
        item.setCurrency(productInfo.currency());
        item.setInStock(productInfo.inStock());
        return itemRepository.save(item);
    }

    @Override
    public void removeItem(Long id) {

        if (!itemRepository.existsById(id)) {
            throw new EntityNotFoundException("Подобного товара не существует: ");
        }
        itemRepository.deleteById(id);
    }
}
