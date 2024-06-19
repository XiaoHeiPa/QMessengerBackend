package org.qbychat.backend.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 *  表定义层。
 *
 * @author zszf
 * @since 2024-06-19
 */
public class GlobalMsgTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final GlobalMsgTableDef GLOBAL_MSG = new GlobalMsgTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");

    
    public final QueryColumn MSG = new QueryColumn(this, "msg");

    
    public final QueryColumn TIME = new QueryColumn(this, "time");

    
    public final QueryColumn SENDER = new QueryColumn(this, "sender");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, SENDER, MSG, TIME};

    public GlobalMsgTableDef() {
        super("", "db_global_msg");
    }

    private GlobalMsgTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public GlobalMsgTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new GlobalMsgTableDef("", "db_global_msg", alias));
    }

}
