package org.qbychat.backend.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qbychat.backend.entity.Group;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {
}
