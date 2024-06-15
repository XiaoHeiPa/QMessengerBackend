package org.qbychat.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qbychat.backend.entity.Account;


@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}

