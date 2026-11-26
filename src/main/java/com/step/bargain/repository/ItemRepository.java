package com.step.bargain.repository;

import com.step.bargain.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Daria Pevets
 **/
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByUrl(String url);

    boolean existsByUrl(String url);
}
