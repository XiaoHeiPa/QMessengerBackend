package org.qbychat.backend.ws.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Request {
    String method;
    JSONObject data;
}
