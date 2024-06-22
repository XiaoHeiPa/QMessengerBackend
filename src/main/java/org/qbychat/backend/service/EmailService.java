package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.Email;

public interface EmailService extends IService<Email> {
    String sendVerifyEmail(Email verifiedEmail);
}
