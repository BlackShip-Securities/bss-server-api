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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "comments")
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "research_id")
    private Research research;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childCommentList = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
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
