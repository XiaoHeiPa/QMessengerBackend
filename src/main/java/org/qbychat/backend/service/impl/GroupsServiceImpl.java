package org.qbychat.backend.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Group;
import org.qbychat.backend.mapper.GroupMapper;
import org.qbychat.backend.service.GroupsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.qbychat.backend.entity.table.GroupsTableDef.GROUPS;

@Service
public class GroupsServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupsService {
    @Resource
    private AccountServiceImpl accountService;


    @Override
    public boolean createGroup(@NotNull Account owner, String name) {
        if (accountService.hasUser(name)) {
            return false; // this name had taken by a user
        }
        Group group = new Group();
        group.setOwner(owner.getId());
        group.setCreateTime(new Date().getTime());
        group.setDescription("The default description. QMessenger Powered. QvQ");
        group.setMembers(new HashSet<>());
        group.setName(name);
        group.setShownName(name);
        if (this.hasGroup(group.getName())) {
            return false;
        }
        this.mapper.insert(group);
        return true;
    }

    @Override
    public boolean deleteGroup(Group group) {
        this.mapper.delete(group);
        return true;
    }

    @Override
    public void addMember(Group group, Account member) {
        group.getMembers().add(member.getId());
        this.mapper.update(group);
    }

    @Override
    public void removeMember(Group group, Account member) {
        group.getMembers().remove(member.getId());
        this.mapper.update(group);
    }

    @Override
    public Group findGroupByName(String groupName) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(GROUPS.ALL_COLUMNS)
                .where(GROUPS.GROUP_NAME.eq(groupName));
        return this.mapper.selectOneByQuery(qw);
    }

    @Override
    public Group findGroupById(Integer id) {
        QueryWrapper qw = new QueryWrapper();
        qw.select(GROUPS.ALL_COLUMNS)
                .where(GROUPS.ID.eq(id));
        return this.mapper.selectOneByQuery(qw);
    }

    @Override
    public void updateGroup(Group group) {
        this.mapper.update(group);
    }

    @Override
    public boolean hasGroup(String name) {
        return this.findGroupByName(name) != null;
    }

    @Override
    public boolean hasGroup(Integer id) {
        return this.findGroupById(id) != null;
    }

    @Override
    public List<Group> queryJoinedGroups(Account user) {
        List<Group> groups = this.mapper.selectAll();
        List<Group> result = new ArrayList<>();
        for (Group group : groups) {
            if (group.getMembers().contains(user.getId())) {
                result.add(group);
            }
        }
        return result;
    }
}
