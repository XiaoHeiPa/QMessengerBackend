package org.qbychat.backend.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendDTO {
    private Integer id;
    private String username;
    private String nickname;
}
