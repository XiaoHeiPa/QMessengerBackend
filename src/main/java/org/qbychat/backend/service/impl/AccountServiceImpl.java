package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.table.AccountTableDef;
import org.qbychat.backend.mapper.AccountMapper;
import org.qbychat.backend.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 服务层实现。
 *
 * @author zszf
 * @since 2024-06-15
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return User.withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public void registerAccount(Account account) {
        this.mapper.insert(account);
    }

    public Account findAccountByNameOrEmail(String username) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(AccountTableDef.getInstance().ALL_COLUMNS)
                .where(AccountTableDef.getInstance().USERNAME.eq(username))
                .or(AccountTableDef.getInstance().EMAIL.eq(username));
        return this.mapper.selectOneByQuery(qw);
    }

    public Account findAccountById(Integer id) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(AccountTableDef.getInstance().ALL_COLUMNS)
                .where(AccountTableDef.getInstance().ID.eq(id));
        return this.mapper.selectOneByQuery(qw);
    }

    public void changeAccountRole(Account account, String roles) {
        Account newAccount = UpdateEntity.of(Account.class, account.getId());
        newAccount.setRole(roles);
        this.mapper.update(newAccount);
    }
}
