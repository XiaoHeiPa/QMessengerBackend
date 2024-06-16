package org.qbychat.backend.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qbychat.backend.entity.Account;

/**
 * 映射层。
 *
 * @author zszf
 * @since 2024-06-15
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
