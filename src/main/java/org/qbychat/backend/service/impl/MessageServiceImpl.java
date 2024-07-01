package org.qbychat.backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.qbychat.backend.entity.ChatMessage;
import org.qbychat.backend.mapper.MessageMapper;
import org.qbychat.backend.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, ChatMessage> implements MessageService {
    // TODO
}
