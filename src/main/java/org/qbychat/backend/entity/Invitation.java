package org.qbychat.backend.entity;

import lombok.Data;

@Data
public class Invitation {
    private String code;
    private int adminId;
    private Long expire;
}
