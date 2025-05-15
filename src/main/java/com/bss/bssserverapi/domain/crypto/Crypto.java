package com.bss.bssserverapi.domain.crypto;

import jakarta.persistence.*;

@Entity
public class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;
}
