package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.qbychat.backend.entity.ClientVerify;
import org.qbychat.backend.entity.table.ClientVerifyTableDef;
import org.qbychat.backend.mapper.ClientVerifyMapper;
import org.qbychat.backend.service.ClientVerifyService;
import org.springframework.stereotype.Service;

@Service
public class ClientVerifyServiceImpl extends ServiceImpl<ClientVerifyMapper, ClientVerify> implements ClientVerifyService {
    @Override
    public ClientVerify auth(String hash) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(ClientVerifyTableDef.VERIFY_INFO.ALL_COLUMNS)
                .where(ClientVerifyTableDef.VERIFY_INFO.HASH.eq(hash));
        return this.mapper.selectOneByQuery(qw);
    }

    @Override
    public void update(ClientVerify clientVerify) {
        this.mapper.insert(clientVerify);
    }
}
