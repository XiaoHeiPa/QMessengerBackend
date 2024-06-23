package org.qbychat.backend.ws.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Location {
    double longitude; // 经度
    double latitude; // 纬度
}
