package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.qbychat.backend.entity.ChatMessage;
import org.qbychat.backend.mapper.MessageMapper;
import org.qbychat.backend.service.MessageService;
import org.springframework.boot.actuate.autoconfigure.data.redis.RedisHealthContributorAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.qbychat.backend.entity.table.MessagesTableDef.MESSAGES;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, ChatMessage> implements MessageService {
    private final RedisHealthContributorAutoConfiguration redisHealthContributorAutoConfiguration;

    public MessageServiceImpl(RedisHealthContributorAutoConfiguration redisHealthContributorAutoConfiguration) {
        this.redisHealthContributorAutoConfiguration = redisHealthContributorAutoConfiguration;
    }

    @Override
    public void addMessage(ChatMessage message) {
        this.mapper.insert(message);
    }

    @Override
    public void removeMessage(ChatMessage message) {
        this.mapper.delete(message);
    }

    @Override
    public List<ChatMessage> getMessages(Integer channel, boolean isDirectMessage) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .where(MESSAGES.IS_DM.eq(isDirectMessage));
        int size = this.mapper.selectListByQuery(qw).size();
        int pageSize = 100;
        List<ChatMessage> records = new ArrayList<>(this.mapper.paginate(size - 1, pageSize, qw)
                .getRecords());
        records.addAll(this.mapper.paginate(size - 2, pageSize, qw).getRecords());
        return records;
    }
}
