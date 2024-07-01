package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Roles;
import org.qbychat.backend.entity.table.AccountsTableDef;
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

    public Account findAccountByNameOrEmail(String username) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(AccountsTableDef.ACCOUNTS.ALL_COLUMNS)
                .where(AccountsTableDef.ACCOUNTS.USERNAME.eq(username))
                .or(AccountsTableDef.ACCOUNTS.EMAIL.eq(username));
        return this.mapper.selectOneByQuery(qw);
    }

    public Account findAccountById(Integer id) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(AccountsTableDef.ACCOUNTS.ALL_COLUMNS)
                .where(AccountsTableDef.ACCOUNTS.ID.eq(id));
        return this.mapper.selectOneByQuery(qw);
    }

    @Override
    public void updateRole(Account account, Roles role) {
        Account newAccount = UpdateEntity.of(Account.class, account.getId());
        newAccount.setRole(role.name());
        this.updateUser(newAccount);
    }

    public void updatePassword(Account account, String password) {
        Account newAccount = UpdateEntity.of(Account.class, account.getId());
        newAccount.setPassword(password);
        this.updateUser(newAccount);
    }

    @Override
    public void updateUser(Account account) {
        this.mapper.update(account);
    }

    @Override
    public boolean hasUser(String name) {
        return this.findAccountByNameOrEmail(name) != null;
    }

    @Override
    public boolean hasUser(Integer id) {
        return this.findAccountById(id) != null;
    }
}
