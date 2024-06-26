package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Friends;
import org.qbychat.backend.mapper.FriendsMapper;
import org.qbychat.backend.service.FriendsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.qbychat.backend.entity.table.FriendsTableDef.FRIENDS;

/**
 *  服务层实现。
 *
 * @author zszf
 * @since 2024-06-26
 */
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends> implements FriendsService {
    @Resource
    private AccountServiceImpl accountService;

    public Account[] getFriendsWithAccount(Account account) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(FRIENDS.ALL_COLUMNS)
                .where(FRIENDS.FROM.eq(account.getUsername()))
                .or(FRIENDS.FROM.eq(account.getNickname()));
        List<Friends> friends = this.mapper.selectListByQuery(qw);
        List<Account> accounts = new ArrayList<>();
        friends.forEach(i -> {
            accounts.add(accountService.findAccountByNameOrEmail(i.getTo()));
        });
        return accounts.toArray(new Account[0]);
    }

    public void addFriend(Account a, Account b) {
        Friends friends = new Friends();
        friends.setFrom(a.getUsername());
        friends.setTo(b.getUsername());
        friends.setFromId(a.getId());
        friends.setToId(b.getId());
        this.mapper.insert(friends);
    }

    public void removeFriend(Account a, Account b) {
        QueryWrapper qw = new QueryWrapper();
        qw.where(FRIENDS.FROM.eq(a)).and(FRIENDS.TO.eq(b));
        this.mapper.deleteByQuery(qw);
    }
}
