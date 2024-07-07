package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Friend;
import org.qbychat.backend.mapper.FriendsMapper;
import org.qbychat.backend.service.FriendsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.qbychat.backend.entity.table.FriendsTableDef.FRIENDS;

/**
 * 服务层实现。
 *
 * @author zszf
 * @since 2024-06-26
 */
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friend> implements FriendsService {
    @Resource
    private AccountServiceImpl accountService;

    public List<Account> getFriendsWithAccount(Account account) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(FRIENDS.ALL_COLUMNS)
                .where(FRIENDS.USER1.eq(account.getId()))
                .or(FRIENDS.USER2.eq(account.getId()));
        List<Friend> friends = this.mapper.selectListByQuery(qw);
        List<Account> accounts = new ArrayList<>();
        friends.forEach(friend -> {
            if (friend.getUser1().equals(account.getId())) {
                accounts.add(accountService.findAccountById(friend.getUser2()));
            } else {
                accounts.add(accountService.findAccountById(friend.getUser1()));
            }
        });
        return accounts;
    }

    public void addFriend(Account user1, Account user2) {
        Friend friend = new Friend();
        friend.setUser1(user1.getId());
        friend.setUser2(user2.getId());
        friend.setUser1(user1.getId());
        friend.setUser2(user2.getId());
        this.mapper.insert(friend);
    }

    public boolean hasFriend(Account user, Account target) {
        QueryWrapper qw = new QueryWrapper();
        // 双向查询 Powered by ChatGPT
        qw.select(FRIENDS.ALL_COLUMNS)
                .where(FRIENDS.USER1.eq(user.getId()))
                .and(FRIENDS.USER2.eq(target.getId()));
        List<Friend> friends1 = this.mapper.selectListByQuery(qw);

        QueryWrapper qw1 = new QueryWrapper();
        qw1.select(FRIENDS.ALL_COLUMNS)
                .where(FRIENDS.USER2.eq(user.getId()))
                .and(FRIENDS.USER1.eq(target.getId()));
        List<Friend> friends2 = this.mapper.selectListByQuery(qw1);
        return !friends1.isEmpty() || !friends2.isEmpty();
    }

    /**
     * 删除好友
     *
     * @return 是否删除成功
     */
    public boolean removeFriend(Account a, Account b) {
        if (!hasFriend(a, b)) {
            return false;
        }

        QueryWrapper qw = new QueryWrapper();
        qw
                .where(FRIENDS.USER1.eq(a))
                .and(FRIENDS.USER2.eq(b));
        if (!this.mapper.selectListByQuery(qw).isEmpty()) {
            this.mapper.deleteByQuery(qw);
        }

        QueryWrapper qw1 = new QueryWrapper();
        qw1
                .where(FRIENDS.USER2.eq(a))
                .and(FRIENDS.USER1.eq(b));
        if (!this.mapper.selectListByQuery(qw1).isEmpty()) {
            this.mapper.deleteByQuery(qw1);
        }
        return true;
    }
}
