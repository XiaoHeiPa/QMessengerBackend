package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 服务层。
 *
 * @author zszf
 * @since 2024-06-15
 */
public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    void registerAccount(Account account);

    Account findAccountById(Integer id);

    void updateRole(Account account, String roles);
}
