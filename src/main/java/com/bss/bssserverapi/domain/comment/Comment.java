package com.bss.bssserverapi.domain.comment;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.global.common.DateTimeField;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comment_paging", columnList = "research_id, parent_comment_id, id DESC")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends DateTimeField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private Long childCommentCount = 0L;

    private Long recommendCount = 0L;

    @NotNull
    private Boolean isDeleted = Boolean.FALSE;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "research_id")
    private Research research;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentComment")
    private List<Comment> childCommentList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Builder
    public Comment(final String content, final User user, final Research research) {

        this.content = content;
        this.user = user;
        this.research = research;
    }

    public void setParentComment(final Comment parentComment){

        this.parentComment = parentComment;
        this.parentComment.addChildContent(this);
    }

    public void addChildContent(final Comment childComment){

        this.childCommentList.add(childComment);
        this.childCommentCount++;
    }

    public void update(String content) {

        this.content = content;
    }

    public void softDelete() {

        this.isDeleted = Boolean.TRUE;
        this.deletedAt = LocalDateTime.now();

        this.research.minusComment();
    }

    public void addRecommend(){

        this.recommendCount++;
    }

    public void minusRecommend(){

        this.recommendCount--;
    }
}
