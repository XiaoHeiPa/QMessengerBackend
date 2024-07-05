package org.qbychat.backend.ws.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Response {
    public static final Response LOCATE = new Response("locate");

    public static final Response HAS_FRIEND = new Response("has-friend");
    public static final Response FRIEND_REQUEST = new Response("friend-request");
    public static final Response FRIEND_REQUEST_SENT = new Response("friend-request-sent");
    public static final Response CHAT_MESSAGE = new Response("chat-message");
    public static final Response USER_INFO = new Response("user-info");

    private Response(String method) {
        this.method = method;
    }

    public final String method;
    Object data;
    @Builder.Default
    boolean hasError = false;

    public String toJson() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

    public Response setData(Object data) {
        this.data = data;
        return this;
    }
}
