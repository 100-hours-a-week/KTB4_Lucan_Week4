package com.lucan.community.dto.post;

import com.lucan.community.enums.Team;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 26, message = "제목은 최대 26자까지 작성 가능합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "게시판을 선택해주세요.")
    private Team team;

    private MultipartFile imageFile;
}
