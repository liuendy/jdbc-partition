package org.the.force.jdbc.partition.engine.stmt.table;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.stmt.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.stmt.SqlRefer;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/14.
 */
public abstract class QueriedSqlTable implements ConditionalSqlTable {

    private final String alias;

    private final SQLTableSource sqlTableSource;



    public QueriedSqlTable(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLExprTableSource) {
            throw new SqlParseException("sqlTableSource instanceof SQLExprTableSource");
        }
        this.sqlTableSource = sqlTableSource;
        this.alias = sqlTableSource.getAlias();
    }

    public String getAlias() {
        return alias;
    }

    public String getTableName() {
        return null;
    }

    public final boolean equals(Object o) {
        return sqlTableSource.equals(o);

    }

    public final int hashCode() {
        return sqlTableSource.hashCode();
    }

    public final void setAlias(String alias) {

    }

    public abstract List<String> getAllReferAbleLabels();

    public SQLTableSource getSQLTableSource() {
        return sqlTableSource;
    }

    public String getRelativeKey() {
        return alias;
    }

    private final Map<SqlRefer,List<Pair<ConditionalSqlTable,SqlRefer>>> equalReferMap = new LinkedHashMap<>();

    private SQLExpr tableOwnCondition;//归集到currentSqlTable的sql条件


    public Map<SqlRefer,List<Pair<ConditionalSqlTable,SqlRefer>>> getEqualReferMap() {
        return equalReferMap;
    }

    public SQLExpr getTableOwnCondition() {
        return tableOwnCondition;
    }

    public void setTableOwnCondition(SQLExpr tableOwnCondition) {
        this.tableOwnCondition = tableOwnCondition;
    }

    private final Map<SqlRefer, List<SqlExprEvaluator>> columnConditionsMap = new LinkedHashMap<>();

    private final Map<List<SQLExpr>, SQLInListEvaluator> columnInListConditionMap = new LinkedHashMap<>();

    public Map<SqlRefer, List<SqlExprEvaluator>> getColumnConditionsMap() {
        return columnConditionsMap;
    }

    public Map<List<SQLExpr>, SQLInListEvaluator> getColumnInListConditionMap() {
        return columnInListConditionMap;
    }
}
