package org.qbychat.backend.entity.vo;

import lombok.Data;
import org.qbychat.backend.entity.Role;

import java.util.Date;

@Data
public class AccountVO {
    private Integer id;
    private String username;
    private String email;
    private Role role;
    private Long registerTime;
    private String nickname;
}
