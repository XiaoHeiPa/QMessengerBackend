package org.qbychat.backend.ws.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum Response {
    LOCATE("locate", null),
    HAS_FRIEND("has-friend", null),
    FRIEND_REQUEST("friend-request", null),
    FRIEND_REQUEST_SENT("friend-request-sent", null),
    CHAT_MESSAGE("chat-message", null);

    public final String method;
    Object data;

    public String toJson() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

    public Response setData(Object data) {
        this.data = data;
        return this;
    }

    public class FRIEND_REQUEST {
    }
}
