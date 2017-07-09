package org.the.force.jdbc.partition.engine.executor.plan.dql;

import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSourceImpl;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.engine.executor.plan.QueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.List;

/**
 * Created by xuji on 2017/7/1.
 */
public abstract class UnionQueryPlan extends SQLTableSourceImpl implements QueryPlan {

    private final LogicDbConfig logicDbConfig;

    public UnionQueryPlan(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    protected void accept0(SQLASTVisitor visitor) {

    }

    public String getAlias() {
        return null;
    }

    public void setAlias(String alias) {

    }

    public List<SQLHint> getHints() {
        return null;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }
}