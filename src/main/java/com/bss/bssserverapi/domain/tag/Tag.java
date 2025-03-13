package com.bss.bssserverapi.domain.tag;

import com.bss.bssserverapi.domain.research.ResearchTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 25)
    private String name;

    @CreatedDate
    @Column(nullable=false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.PERSIST)
    private List<ResearchTag> researchTagList = new ArrayList<>();

    public Tag(final String name) {

        this.name = name;
    }

    public void addResearchTag(final ResearchTag researchTag) {

        researchTag.setTag(this);
        this.researchTagList.add(researchTag);
    }
}
