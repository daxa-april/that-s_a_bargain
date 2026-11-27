package com.step.bargain.service.impl;

import com.step.bargain.MarketplaceParser.MarketplaceParser;
import com.step.bargain.model.entity.Item;
import com.step.bargain.model.entity.Platform;
import com.step.bargain.model.entity.dto.ProductInfo;
import com.step.bargain.repository.ItemRepository;
import com.step.bargain.service.ItemService;
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
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final Map<Platform, MarketplaceParser> parsers;

    public ItemServiceImpl(List<MarketplaceParser> parserList, ItemRepository itemRepository) {
        this.parsers = parserList.stream()
                .collect(Collectors.toMap(MarketplaceParser::getPlatform, Function.identity()));
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public Item addItem(String url, Long chatId) {
        if (itemRepository.existsByUrlAndChatId(url, chatId)) {
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
        item.setChatId(chatId);
        item.setTitle(productInfo.title());
        item.setPlatform(platform);
        item.setLastPrice(productInfo.price());
        item.setCurrency(productInfo.currency());
        item.setInStock(productInfo.inStock());
        return itemRepository.save(item);
    }

    @Override
    public void removeItem(Long id, Long chatId) {

        Item item = itemRepository.findByIdAndChatId(id, chatId)
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден или уже удален"));
        itemRepository.delete(item);
    }
}

