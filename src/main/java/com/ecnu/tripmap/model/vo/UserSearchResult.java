package com.ecnu.tripmap.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResult {
    private Integer userId;

    private String userNickname;

    private String userAvatar;

    private boolean isFollowed;
}
