package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Roles;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 服务层。
 *
 * @author zszf
 * @since 2024-06-15
 */
public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    Account findAccountById(Integer id);

    void updateRole(Account account, Roles roles);
    void updatePassword(Account account, String password);
    void updateUsername(Account user, String username);

    void updateNickname(Account account, String nickname);

    void updateUser(Account account);

    boolean hasUser(String name);

    boolean hasUser(Integer id);


    void updateBio(Account account, String bio);
}
