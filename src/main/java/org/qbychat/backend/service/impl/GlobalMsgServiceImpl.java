package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.qbychat.backend.entity.GlobalMsg;
import org.qbychat.backend.mapper.GlobalMsgMapper;
import org.qbychat.backend.service.GlobalMsgService;
import org.springframework.stereotype.Service;

import static org.qbychat.backend.entity.table.GlobalMsgTableDef.GLOBAL_MSG;

/**
 *  服务层实现。
 *
 * @author zszf
 * @since 2024-06-19
 */
@Service
public class GlobalMsgServiceImpl extends ServiceImpl<GlobalMsgMapper, GlobalMsg> implements GlobalMsgService {

    @Override
    public GlobalMsg findGlobalMsgById(String id) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(GLOBAL_MSG.ALL_COLUMNS)
                .where(GLOBAL_MSG.ID.eq(id));
        return this.mapper.selectOneByQuery(qw);
    }

    @Override
    public GlobalMsg findGlobalMsgBySender(String sender) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(GLOBAL_MSG.ALL_COLUMNS)
                .where(GLOBAL_MSG.SENDER.eq(sender));
        return this.mapper.selectOneByQuery(qw);
    }
}
