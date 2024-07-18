package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.qbychat.backend.entity.ChatMessage;
import org.qbychat.backend.mapper.MessageMapper;
import org.qbychat.backend.service.MessageService;
import org.qbychat.backend.utils.CryptUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.qbychat.backend.entity.table.MessagesTableDef.MESSAGES;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, ChatMessage> implements MessageService {
    @Value("${messenger.message.page.size}")
    private int pageSize;

    @Resource
    CryptUtils encryptUtils;

    @SneakyThrows
    @Override
    public void addMessage(ChatMessage message) {
        ChatMessage.MessageContent content = message.getContent();
        content.setText(encryptUtils.encryptString(content.getText()));
        this.mapper.insert(message);
    }

    @Override
    public void removeMessage(ChatMessage message) {
        this.mapper.delete(message);
    }

    @Override
    public void removeMessage(int id) {
        this.mapper.deleteById(id);
    }

    @Override
    public List<ChatMessage> fetchLatestGroupMessages(int channel) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .and(MESSAGES.IS_DM.eq(false));
        int total = this.mapper.selectListByQuery(qw).size();
        List<ChatMessage> records = new ArrayList<>();
        if (total / pageSize < 1) {
            records.addAll(this.mapper.paginate(1, pageSize, qw).getRecords());
        }
        if (total / pageSize > 1) {
            records.addAll(this.mapper.paginate(total / pageSize, pageSize, qw).getRecords());
        }
        return records;
    }

    @Override
    public List<ChatMessage> fetchLatestDirectMessages(int user1, int user2) {
        List<ChatMessage> records  = new ArrayList<>();
        queryMessages(user1, user2, records);
        queryMessages(user2, user1, records);
        return records;
    }

    private void queryMessages(int user1, int user2, List<ChatMessage> records) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.SENDER.eq(user1))
                .and(MESSAGES.TO.eq(user2))
                .and(MESSAGES.IS_DM.eq(true));
        int total = this.mapper.selectListByQuery(qw).size();
        if (total / pageSize < 1) {
            records.addAll(this.mapper.paginate(1, pageSize, qw).getRecords());
        }
        if (total / pageSize > 1) {
            records.addAll(this.mapper.paginate(total / pageSize, pageSize, qw).getRecords());
        }
    }

    @Override
    public List<ChatMessage> fetchGroupMessages(int channel, int page) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .and(MESSAGES.IS_DM.eq(false));
        int total = this.mapper.selectListByQuery(qw).size();
        return this.mapper.paginate(total - page, pageSize, qw).getRecords();
    }

    @Override
    public List<ChatMessage> fetchDirectMessages(int user1, int user2, int page) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.SENDER.eq(user1))
                .and(MESSAGES.TO.eq(user2))
                .and(MESSAGES.IS_DM.eq(true));
        int total = this.mapper.selectListByQuery(qw).size();
        List<ChatMessage> records = new ArrayList<>(this.mapper.paginate(total, pageSize, qw).getRecords());
        
        QueryWrapper qw1 = new QueryWrapper();
        qw1.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.SENDER.eq(user2))
                .and(MESSAGES.TO.eq(user1))
                .and(MESSAGES.IS_DM.eq(true));
        int total1 = this.mapper.selectListByQuery(qw).size();
        records.addAll(this.mapper.paginate(total1, pageSize, qw1).getRecords());
        
        return records;
    }

    @Override
    public List<ChatMessage> fetchMessages(int channel, boolean isDM, ChatMessage since) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .and(MESSAGES.IS_DM.eq(isDM));
        List<ChatMessage> chatMessages = this.mapper.selectListByQuery(qw);
        int pos = chatMessages.indexOf(since);
        int atPage = pos / pageSize + 1;
        return this.mapper.paginate(atPage, pageSize, qw).getRecords();
    }
}
