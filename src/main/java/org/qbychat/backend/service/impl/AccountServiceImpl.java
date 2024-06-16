package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.mapper.AccountMapper;
import org.qbychat.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.qbychat.backend.entity.table.AccountTableDef.ACCOUNT;

/**
 * 服务层实现。
 *
 * @author zszf
 * @since 2024-06-15
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Autowired
    public AccountMapper accountMapper;

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
        Account name = accountMapper.selectOneByQuery(QueryWrapper.create().where(ACCOUNT.USERNAME.eq(username)));
        Account email = accountMapper.selectOneByQuery(QueryWrapper.create().where(ACCOUNT.EMAIL.eq(username)));
        return Objects.requireNonNullElse(name, email);
    }
}
