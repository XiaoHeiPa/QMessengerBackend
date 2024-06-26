package org.qbychat.backend.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qbychat.backend.entity.Email;

@Mapper
public interface EmailMapper extends BaseMapper<Email> {
}
