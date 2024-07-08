package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.qbychat.backend.entity.Message;
import org.qbychat.backend.mapper.MessageMapper;
import org.qbychat.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.qbychat.backend.entity.table.MessagesTableDef.MESSAGES;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Value("${messenger.message.page.size}")
    private int pageSize;

    @Override
    public void addMessage(Message message) {
        this.mapper.insert(message);
    }

    @Override
    public void removeMessage(Message message) {
        this.mapper.delete(message);
    }

    @Override
    public void removeMessage(int id) {
        this.mapper.deleteById(id);
    }

    @Override
    public List<Message> fetchLatestGroupMessages(int channel) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .and(MESSAGES.IS_DM.eq(false));
        int total = this.mapper.selectListByQuery(qw).size();
        List<Message> records;
        if (total == 0) {
            return List.of();
        } else {
            records = new ArrayList<>(this.mapper.paginate(total, pageSize, qw).getRecords());
        }
        if (total > 1) {
            records.addAll(this.mapper.paginate(total - 1, pageSize, qw).getRecords());
        }
        return records;
    }

    @Override
    public List<Message> fetchLatestDirectMessages(int user1, int user2) {
        List<Message> records  = new ArrayList<>();
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
        QueryWrapper qw1 = new QueryWrapper();
        qw1.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.SENDER.eq(user2))
                .and(MESSAGES.TO.eq(user1))
                .and(MESSAGES.IS_DM.eq(true));
        int total1 = this.mapper.selectListByQuery(qw1).size();
        if (total1 / pageSize < 1) {
            records.addAll(this.mapper.paginate(1, pageSize, qw1).getRecords());
        }
        if (total1 / pageSize > 1) {
            records.addAll(this.mapper.paginate(total1 / pageSize, pageSize, qw1).getRecords());
        }
        return records;
    }

    @Override
    public List<Message> fetchGroupMessages(int channel, int page) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .and(MESSAGES.IS_DM.eq(false));
        int total = this.mapper.selectListByQuery(qw).size();
        return this.mapper.paginate(total - page, pageSize, qw).getRecords();
    }

    @Override
    public List<Message> fetchDirectMessages(int user1, int user2, int page) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.SENDER.eq(user1))
                .and(MESSAGES.TO.eq(user2))
                .and(MESSAGES.IS_DM.eq(true));
        int total = this.mapper.selectListByQuery(qw).size();
        List<Message> records = new ArrayList<>(this.mapper.paginate(total, pageSize, qw).getRecords());
        
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
    public List<Message> fetchMessages(int channel, boolean isDM, Message since) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .and(MESSAGES.IS_DM.eq(isDM));
        List<Message> chatMessages = this.mapper.selectListByQuery(qw);
        int pos = chatMessages.indexOf(since);
        int atPage = pos / pageSize + 1;
        return this.mapper.paginate(atPage, pageSize, qw).getRecords();
    }
}
