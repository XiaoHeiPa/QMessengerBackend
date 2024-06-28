package org.qbychat.backend.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

public class GroupsTableDef extends TableDef {
    @Serial
    private static final long serialVersionUID = 1L;


    public final QueryColumn ID = new QueryColumn(this, "id");


    public final QueryColumn GROUP_NAME = new QueryColumn(this, "name");


    public final QueryColumn CREATE_TIME = new QueryColumn(this, "createTime");
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    public final QueryColumn OWNER = new QueryColumn(this, "owner");
    public final QueryColumn MEMBERS = new QueryColumn(this, "members");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, GROUP_NAME, DESCRIPTION, CREATE_TIME, OWNER, MEMBERS};


    public static final GroupsTableDef GROUPS = new GroupsTableDef();

    public GroupsTableDef() {
        super("", "db_groups");
    }
}
