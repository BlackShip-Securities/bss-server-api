package com.bss.bssserverapi.domain.research;

import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
import com.bss.bssserverapi.domain.stock.Stock;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.global.common.DateTimeField;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    private Long recommendCount = 0L;

    private Long commentCount = 0L;

    @Column(nullable = false)
    private Long targetPrice;

    @Column(nullable = false)
    private LocalDate dateStart;

    @Column(nullable = false)
    private LocalDate dateEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @OneToMany(mappedBy = "research", cascade = CascadeType.PERSIST)
    private List<ResearchTag> researchTagList = new ArrayList<>();

    public Research(String title, String content, Long targetPrice, LocalDate dateStart, LocalDate dateEnd) {

        this.title = title;
        this.content = content;
        this.targetPrice = targetPrice;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public static Research toEntity(CreateResearchReqDto getResearchResDto) {

        return new Research(
                getResearchResDto.getTitle(),
                getResearchResDto.getContent(),
                getResearchResDto.getTargetPrice(),
                getResearchResDto.getDateStart(),
                getResearchResDto.getDateEnd());
    }

    public void setUser(final User user) {

        this.user = user;
    }

    public void setStock(final Stock stock){

        this.stock = stock;
    }

    public void addResearchTag(final ResearchTag researchTag) {

        researchTag.setResearch(this);
        this.researchTagList.add(researchTag);
    }

    public void addRecommend(){

        this.recommendCount++;
    }

    public void minusRecommend(){

        this.recommendCount--;
    }

    public void addComment() {

        this.commentCount++;
    }
}