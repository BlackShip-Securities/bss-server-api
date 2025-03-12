package com.bss.bssserverapi.domain.user;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.global.common.DateTimeField;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends DateTimeField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String userName;

    @Column(nullable = false, length = 255)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Research> researchList = new ArrayList<>();

    @Builder
    public User(final String userName, final String password) {

        this.userName = userName;
        this.password = password;
    }

    public void addResearch(final Research research) {

        this.researchList.add(research);
        research.setUser(this);
    }
}
