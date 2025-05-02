package com.bss.bssserverapi.fixture;

import com.bss.bssserverapi.domain.comment.Comment;
import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommentFixture {

    public static final Comment 댓글_생성_성공(final String content) {

        User commentUser = UserFixture.사용자_0();
        User researchUser = UserFixture.사용자_1();
        Research research = ResearchFixture.리서치_생성_성공();
        research.setUser(researchUser);

        return Comment.builder()
                .user(commentUser)
                .research(research)
                .content(content)
                .build();
    }

    public static final Comment 답글_생성_성공(final Comment parentComment, final String content) {

        User commentUser = UserFixture.사용자_0();
        User researchUser = UserFixture.사용자_1();
        Research research = ResearchFixture.리서치_생성_성공();
        research.setUser(researchUser);

        Comment comment = Comment.builder()
                .user(commentUser)
                .research(research)
                .content(content)
                .build();

        comment.setParentComment(parentComment);

        return comment;
    }

    public static final List<Comment> 댓글_페이징_조회(final Long batchSize) {

        User user = UserFixture.사용자_0();
        Research research = ResearchFixture.리서치_생성_성공();
        List<Comment> commentList = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            commentList.add(Comment.builder()
                    .user(user)
                    .research(research)
                    .content("original")
                    .build());
        }

        return commentList;
    }
}
