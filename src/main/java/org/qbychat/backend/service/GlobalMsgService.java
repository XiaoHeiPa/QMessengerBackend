package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.GlobalMsg;

/**
 *  服务层。
 *
 * @author zszf
 * @since 2024-06-19
 */
public interface GlobalMsgService extends IService<GlobalMsg> {
    GlobalMsg findGlobalMsgById(String id);
    GlobalMsg findGlobalMsgBySender(String sender);

    void createGlobalMsg(GlobalMsg globalMsg);

    void getNextId();
}
