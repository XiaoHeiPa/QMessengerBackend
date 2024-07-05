package org.qbychat.backend.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qbychat.backend.entity.Message;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
