package org.the.force.jdbc.partition.engine.executor.plan;

import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

/**
 * Created by xuji on 2017/6/4.
 */
public interface QueryPlan extends SqlExecutionPlan, SQLTableSource {
}