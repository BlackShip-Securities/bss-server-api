package com.bss.bssserverapi.domain.research;

import com.bss.bssserverapi.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResearchRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean recommend;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "researchId")
    private Research research;

    @Builder
    public ResearchRecommend(final Boolean recommend, final User user, final Research research) {

        this.recommend = recommend;
        this.user = user;
        this.research = research;
    }

    public void updateRecommend() {

        if(this.recommend.equals(Boolean.TRUE)){
            this.recommend = Boolean.FALSE;
            this.research.minusRecommend();
        }
        else if(this.recommend.equals(Boolean.FALSE)){
            this.recommend = Boolean.TRUE;
            this.research.addRecommend();
        }
    }
}
