package org.qbychat.backend.ws;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.ws.entity.Location;
import org.qbychat.backend.ws.entity.Request;
import org.qbychat.backend.ws.entity.RequestType;
import org.qbychat.backend.ws.entity.Response;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.qbychat.backend.utils.Const.DEVICE_LOCATION;

@Log4j2
@Component
public class FindDeviceHandler extends AuthedTextHandler {
    public static ConcurrentHashMap<String, WebSocketSession> connections = new ConcurrentHashMap<>();
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public List<String> findDeviceMeidsByAccount(Account account) {
        List<String> list = new ArrayList<>();
        connections.forEach((meid, session) -> {
            if (getUser(session).getId().equals(account.getId())) {
                list.add(meid);
            }
        });
        return list;
    }

    @Override
    protected void afterAuthorization(@NotNull WebSocketSession session, Account account) throws Exception {
        List<String> list = session.getHandshakeHeaders().get("meid");
        if (list == null) {
            session.close(); // stop connecting with PostMan, lol
            return;
        }
        String meid = list.get(0);
        if (connections.containsKey(meid)) {
            connections.get(meid).close(); // 关闭旧连接
            // todo 这样做似乎是不安全的, 但是没有更好的解决方案.
        }
        connections.put(meid, session);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        Request request = JSON.parseObject(message.getPayload(), Request.class);
        if (request.getMethod().equals(RequestType.UPDATE_LOCATION)) {
            Location location = JSON.parseObject(JSON.toJSONString(request.getData()), Location.class);
            redisTemplate.opsForValue().set(DEVICE_LOCATION + getMeid(session), location);
        }
    }

    protected String getMeid(WebSocketSession session) {
        if (!connections.containsValue(session)) {
            return null; // unreachable
        }
        for (String key : connections.keySet()) {
            if (connections.get(key).equals(session)) {
                return key; // todo 解决史山
            }
        }
        return null;
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        Account account = getUser(session);
        log.info("User {} has disconnected from {}", account.getId(), this.getClass().getName());
        connections.remove(getMeid(session));
    }

    public Location findLocationByMeid(String meid) {
        return (Location) redisTemplate.opsForValue().get(DEVICE_LOCATION + meid);
    }

    public void sendLocateRequest(String meid) throws Exception {
        WebSocketSession session = connections.get(meid);
        session.sendMessage(new TextMessage(Response.LOCATE.toJson()));
    }
}
