package org.qbychat.backend.ws.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Response {
    public static Response LOCATE = new Response("locate", null);

    String method;
    JSONObject data;

    public String toJson()  {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
