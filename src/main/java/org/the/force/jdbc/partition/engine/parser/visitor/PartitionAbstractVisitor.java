package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.jdbc.partition.engine.executor.query.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.query.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.query.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.query.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.query.tablesource.UnionQueriedTableSource;

/**
 * Created by xuji on 2017/7/14.
 */
public abstract class PartitionAbstractVisitor extends AbstractVisitor implements PartitionSqlASTVisitor{

    public boolean visit(ExitsSubQueriedExpr x) {
        return isContinue();
    }

    public boolean visit(SQLInSubQueriedExpr x) {
        return isContinue();
    }

    public boolean visit(ParallelJoinedTableSource parallelJoinedTableSource){
        return isContinue();
    }

    public boolean visit(SubQueriedTableSource subQueriedTableSource){
        return isContinue();
    }

    public boolean visit(UnionQueriedTableSource unionQueriedTableSource){
        return isContinue();
    }
}
