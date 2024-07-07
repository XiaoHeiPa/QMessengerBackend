package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Friend;

import java.util.List;

/**
 *  服务层。
 *
 * @author zszf
 * @since 2024-06-26
 */
public interface FriendsService extends IService<Friend> {
    List<Account> getFriendsWithAccount(Account account); // 获取此Account下的所有好友
    void addFriend(Account a, Account b);
    boolean removeFriend(Account a, Account b); // a删除b 但b不删除a
}
