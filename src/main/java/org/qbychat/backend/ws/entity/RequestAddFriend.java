package org.qbychat.backend.ws.entity;

import lombok.Data;

@Data
public class RequestAddFriend {
    Integer from = null; // 服务端自动生成
    Integer target;
}
