package org.qbychat.backend.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qbychat.backend.entity.GlobalMsg;

/**
 *  映射层。
 *
 * @author zszf
 * @since 2024-06-19
 */
@Mapper
public interface GlobalMsgMapper extends BaseMapper<GlobalMsg> {

}
