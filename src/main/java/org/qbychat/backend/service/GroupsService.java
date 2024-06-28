package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Group;

public interface GroupsService extends IService<Group> {
    boolean createGroup(Account owner, String name);
    boolean deleteGroup(Group group);

    void addMember(Group group, Account member);
    void removeMember(Group group, Account member);

    Group getGroupByName(String groupName);

    Group getGroupById(Integer id);

    boolean hasGroup(String name);

    boolean hasGroup(Integer id);
}
