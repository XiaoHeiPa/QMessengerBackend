package org.qbychat.backend.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

public class MessagesTableDef extends TableDef {
    @Serial
    private static final long serialVersionUID = 1L;

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn SENDER = new QueryColumn(this, "sender");
    public final QueryColumn TO = new QueryColumn(this, "to");
    public final QueryColumn TYPE = new QueryColumn(this, "type");
    public final QueryColumn TIMESTAMP = new QueryColumn(this, "timestamp");
    public final QueryColumn CONTENT = new QueryColumn(this, "content");
    public final QueryColumn IS_DM = new QueryColumn(this, "is_dm");


    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, SENDER, TO, TYPE, TIMESTAMP, CONTENT, IS_DM};


    public static final MessagesTableDef MESSAGES = new MessagesTableDef();

    public MessagesTableDef() {
        super("", "db_messages");
    }
}
