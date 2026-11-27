package com.step.bargain.repository;

import com.step.bargain.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Daria Pevets
 **/
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndChatId(Long id, Long chatId);

    boolean existsByUrlAndChatId(String url, Long chatId);

    List<Item> findAllByChatId(Long chatId);
}
