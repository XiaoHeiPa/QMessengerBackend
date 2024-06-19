package org.qbychat.backend.entity;

import lombok.Data;

@Data
public class verify implements BaseData {
    private String email_verify_url;
}
