package com.lucan.community.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostPageResponse {

    private List<PostListResponse> posts;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;
}
