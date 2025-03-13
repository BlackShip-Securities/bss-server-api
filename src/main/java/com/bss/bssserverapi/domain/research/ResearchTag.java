package com.bss.bssserverapi.domain.research;

import com.bss.bssserverapi.domain.tag.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ResearchTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "research_id")
    private Research research;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public void setResearch(final Research research) {

        this.research = research;
    }

    public void setTag(final Tag tag) {

        this.tag = tag;
    }
}
