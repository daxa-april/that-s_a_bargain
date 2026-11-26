package com.step.bargain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Daria Pevets
 **/
@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, unique = true, length = 1000)
    private String url;

    @Column(name = "current_price", precision = 12, scale = 2)
    private BigDecimal lastPrice;

    @Column(nullable = false)
    private boolean inStock = false;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String currency;
}
