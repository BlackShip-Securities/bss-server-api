package com.bss.bssserverapi.domain.research;

import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.global.common.DateTimeField;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Research extends DateTimeField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long targetPrice;

    @Column(nullable = false)
    private LocalDate dateStart;

    @Column(nullable = false)
    private LocalDate dateEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "research", cascade = CascadeType.PERSIST)
    private List<ResearchTag> researchTagList = new ArrayList<>();

    @Builder
    public Research(final String title, final String content, final Long targetPrice, final LocalDate dateStart, final LocalDate dateEnd) {

        this.title = title;
        this.content = content;
        this.targetPrice = targetPrice;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public void setUser(final User user) {

        this.user = user;
    }

    public void addResearchTag(final ResearchTag researchTag) {

        researchTag.setResearch(this);
        this. researchTagList.add(researchTag);
    }

    public static Research toEntity(CreateResearchReqDto getResearchResDto) {

        return Research.builder()
                .title(getResearchResDto.getTitle())
                .content(getResearchResDto.getContent())
                .targetPrice(getResearchResDto.getTargetPrice())
                .dateStart(getResearchResDto.getDateStart())
                .dateEnd(getResearchResDto.getDateEnd())
                .build();
    }
}
