package org.qbychat.backend.service;

import com.mybatisflex.core.service.IService;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Group;

import java.util.List;

public interface GroupsService extends IService<Group> {
    boolean createGroup(Account owner, String name);
    boolean deleteGroup(Group group);

    void addMember(Group group, Account member);
    void removeMember(Group group, Account member);

    Group findGroupByName(String groupName);

    Group findGroupById(Integer id);

    boolean hasGroup(String name);

    boolean hasGroup(Integer id);

    List<Group> queryJoinedGroups(Account user);

    void updateGroup(Group group);

    List<Integer> queryMembers(Group group, Integer pageSize, Integer pageNo);

    List<Integer> queryMembers(Integer id, Integer pageSize, Integer pageNo);
}
