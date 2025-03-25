package com.bss.bssserverapi.fixture;

import com.bss.bssserverapi.domain.tag.Tag;

import java.util.List;

public class TagFixture {

    public static final List<Tag> 태그5개() {

        return List.of(
                new Tag("Tag_0"),
                new Tag("Tag_1"),
                new Tag("Tag_2"),
                new Tag("Tag_3"),
                new Tag("Tag_4")
        );
    }
}
