package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Friends;

/**
 *  服务层。
 *
 * @author zszf
 * @since 2024-06-26
 */
public interface FriendsService extends IService<Friends> {
    Account[] getFriendsWithAccount(Account account); // 获取此Account下的所有好友
    void addFriend(Account a, Account b); // 单向好友， 如需双向请运行两次此函数并将实参颠倒。
    void removeFriend(Account a, Account b); // a删除b 但b不删除a
}
