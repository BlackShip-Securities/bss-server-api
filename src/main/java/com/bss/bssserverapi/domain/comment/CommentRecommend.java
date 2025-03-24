package com.bss.bssserverapi.domain.comment;

import com.bss.bssserverapi.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean recommend;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public CommentRecommend(final Boolean recommend, final User user, final Comment comment) {

        this.recommend = recommend;
        this.user = user;
        this.comment = comment;
    }

    public void updateRecommend() {

        if(this.recommend.equals(Boolean.TRUE)){
            this.recommend = Boolean.FALSE;
            this.comment.minusRecommend();
        }
        else if(this.recommend.equals(Boolean.FALSE)){
            this.recommend = Boolean.TRUE;
            this.comment.addRecommend();
        }
    }
}
