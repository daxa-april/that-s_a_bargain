package com.step.bargain.service;

import com.step.bargain.model.entity.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Daria Pevets
 **/
@Service
public interface ItemService {

    Item addItem(String url, Long chatId);

    void removeItem (Long chatId, Long id);
}
