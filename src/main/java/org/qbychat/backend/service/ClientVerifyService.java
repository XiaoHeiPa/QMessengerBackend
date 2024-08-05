package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.ClientVerify;

public interface ClientVerifyService extends IService<ClientVerify> {
    ClientVerify auth(String hash);
    void update(ClientVerify clientVerify);
}
