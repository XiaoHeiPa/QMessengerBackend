package org.qbychat.backend.entity.vo;

import lombok.Data;
import org.qbychat.backend.entity.Role;

import java.util.Date;

@Data
public class AuthorizeVO {
    String username;
    Role role;
    String token;
    String email;
    long expire;
}
