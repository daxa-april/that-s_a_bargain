package com.step.bargain.service;

import com.step.bargain.model.entity.Item;
import org.springframework.stereotype.Service;

/**
 * @author Daria Pevets
 **/
@Service
public interface ClientService {

    Item addItem (String url);

    void removeItem (Long id);
}
