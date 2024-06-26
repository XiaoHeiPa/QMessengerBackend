package org.qbychat.backend.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qbychat.backend.entity.Friends;

/**
 *  映射层。
 *
 * @author zszf
 * @since 2024-06-26
 */
@Mapper
public interface FriendsMapper extends BaseMapper<Friends> {

}
