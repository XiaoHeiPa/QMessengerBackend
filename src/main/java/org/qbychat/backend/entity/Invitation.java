package org.qbychat.backend.entity;

import lombok.Data;

@Data
public class Invitation {
    private String id;
    private Long expire;
}
