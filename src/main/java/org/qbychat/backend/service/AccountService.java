package org.qbychat.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.qbychat.backend.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService, IService<Account> {
    Account findAccountByNameOrEmail(String text);
}
