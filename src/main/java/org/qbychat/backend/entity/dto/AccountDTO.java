package org.qbychat.backend.entity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountDTO {
    private Integer id;
    private String username;
    private String email;
    private String role;
    private Date registerTime;
    private String nickname;
}
