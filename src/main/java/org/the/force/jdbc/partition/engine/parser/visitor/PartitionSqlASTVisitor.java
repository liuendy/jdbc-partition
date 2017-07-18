package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * Created by xuji on 2017/6/9.
 */
public interface PartitionSqlASTVisitor extends MySqlASTVisitor {


    boolean visit(SubQueriedExpr x);

    //boolean visit(SQLInSubQueriedExpr x);

    boolean visit(ParallelJoinedTableSource parallelJoinedTableSource);

}
