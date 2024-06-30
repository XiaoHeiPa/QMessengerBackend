package org.qbychat.backend.ws.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum Response {
    LOCATE("locate"),

    HAS_FRIEND("has-friend"),
    FRIEND_REQUEST("friend-request"),
    FRIEND_REQUEST_SENT("friend-request-sent"),
    CHAT_MESSAGE("chat-message"),
    USER_INFO("user-info");

    public final String method;
    Object data;
    final boolean hasError = false;

    Response(String method) {
        this.method = method;
    }

    @Data
    @AllArgsConstructor
    private static class ResponseJson {
        public final String method;
        Object data;
        boolean hasError = false;
    }

    public String toJson() {
        return JSONObject.toJSONString(new ResponseJson(method, data, hasError), JSONWriter.Feature.WriteNulls);
    }

    public Response setData(Object data) {
        this.data = data;
        return this;
    }

    public class FRIEND_REQUEST {
    }
}
