package com.bss.bssserverapi.domain.stock;

import com.bss.bssserverapi.domain.research.Research;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 6)
    private String stockCode;

    @Column(unique = true, nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Long marketCap;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.PERSIST)
    private List<Research> researchList = new ArrayList<>();

    public Stock(String stockCode, String name, Long marketCap) {

        this.stockCode = stockCode;
        this.name = name;
        this.marketCap = marketCap;
    }

    public void addResearch(final Research research) {

        this.researchList.add(research);
        research.setStock(this);
    }
}
