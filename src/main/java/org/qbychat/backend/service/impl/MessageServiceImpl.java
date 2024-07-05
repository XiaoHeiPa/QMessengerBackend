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
    public List<Message> fetchLatestMessages(Integer channel, boolean isDM) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .and(MESSAGES.IS_DM.eq(isDM));
        int total = this.mapper.selectListByQuery(qw).size();
        List<Message> records;
        if (total == 0) {
            return List.of();
        } else {
            records = new ArrayList<>(this.mapper.paginate(total - 1, pageSize, qw).getRecords());
        }
        if (isDM) {
            // 查询对面发送的信息

            QueryWrapper qw1 = new QueryWrapper();
            qw1.select(MESSAGES.ALL_COLUMNS)
                    .where(MESSAGES.SENDER.eq(channel))
                    .and(MESSAGES.IS_DM.eq(true));
            int total1 = this.mapper.selectListByQuery(qw1).size();
            records.addAll(this.mapper.paginate(total1 - 1, pageSize, qw1).getRecords());
            records.addAll(this.mapper.paginate(total1 - 2, pageSize, qw1).getRecords());
        }
        records.addAll(this.mapper.paginate(total - 2, pageSize, qw).getRecords());
        return records;
    }

    @Override
    public List<Message> fetchMessages(Integer channel, boolean isDM, int page) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .where(MESSAGES.IS_DM.eq(isDM));
        int total = this.mapper.selectListByQuery(qw).size();
        return this.mapper.paginate(total - page, pageSize, qw).getRecords();
    }

    @Override
    public List<Message> fetchMessages(Integer channel, boolean isDM, Message since) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(MESSAGES.ALL_COLUMNS)
                .where(MESSAGES.TO.eq(channel))
                .where(MESSAGES.IS_DM.eq(isDM));
        List<Message> chatMessages = this.mapper.selectListByQuery(qw);
        int pos = chatMessages.indexOf(since);
        int atPage = pos / pageSize + 1;
        return this.mapper.paginate(atPage, pageSize, qw).getRecords();
    }
}
