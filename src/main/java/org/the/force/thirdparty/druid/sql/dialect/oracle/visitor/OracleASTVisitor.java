/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.the.force.thirdparty.druid.sql.dialect.oracle.visitor;

import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.SampleClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleRangeExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCreateIndexStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExitStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExprStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleFileSpecification;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLDateExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.OracleDataTypeTimestamp;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.CycleClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.ModelClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.SearchClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleArgumentExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleIsSetExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleSizeExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterIndexStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterProcedureStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterSynonymStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceAddDataFile;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTriggerStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterViewStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleContinueStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCreateDatabaseDbLinkStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCreatePackageStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleDropDbLinkStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExecuteImmediateStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleForStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleLabelStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleRaiseStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSupplementalIdKey;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSupplementalLogGrp;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleUnique;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

public interface OracleASTVisitor extends SQLASTVisitor {

    void endVisit(OraclePLSQLCommitStatement astNode);

    void endVisit(OracleAnalytic x);

    void endVisit(OracleAnalyticWindowing x);

    void endVisit(SQLDateExpr x);

    void endVisit(OracleDbLinkExpr x);

    void endVisit(OracleDeleteStatement x);

    void endVisit(OracleIntervalExpr x);

    void endVisit(OracleOuterExpr x);

    void endVisit(OracleSelectJoin x);

    void endVisit(OracleSelectPivot x);

    void endVisit(OracleSelectPivot.Item x);

    void endVisit(OracleSelectRestriction.CheckOption x);

    void endVisit(OracleSelectRestriction.ReadOnly x);

    void endVisit(OracleSelectSubqueryTableSource x);

    void endVisit(OracleSelectUnPivot x);

    void endVisit(OracleUpdateStatement x);

    boolean visit(OraclePLSQLCommitStatement astNode);

    boolean visit(OracleAnalytic x);

    boolean visit(OracleAnalyticWindowing x);

    boolean visit(SQLDateExpr x);

    boolean visit(OracleDbLinkExpr x);

    boolean visit(OracleDeleteStatement x);

    boolean visit(OracleIntervalExpr x);

    boolean visit(OracleOuterExpr x);

    boolean visit(OracleSelectJoin x);

    boolean visit(OracleSelectPivot x);

    boolean visit(OracleSelectPivot.Item x);

    boolean visit(OracleSelectRestriction.CheckOption x);

    boolean visit(OracleSelectRestriction.ReadOnly x);

    boolean visit(OracleSelectSubqueryTableSource x);

    boolean visit(OracleSelectUnPivot x);

    boolean visit(OracleUpdateStatement x);

    boolean visit(SampleClause x);

    void endVisit(SampleClause x);

    boolean visit(OracleSelectTableReference x);

    void endVisit(OracleSelectTableReference x);

    boolean visit(PartitionExtensionClause x);

    void endVisit(PartitionExtensionClause x);

    boolean visit(OracleWithSubqueryEntry x);

    void endVisit(OracleWithSubqueryEntry x);

    boolean visit(SearchClause x);

    void endVisit(SearchClause x);

    boolean visit(CycleClause x);

    void endVisit(CycleClause x);

    boolean visit(OracleBinaryFloatExpr x);

    void endVisit(OracleBinaryFloatExpr x);

    boolean visit(OracleBinaryDoubleExpr x);

    void endVisit(OracleBinaryDoubleExpr x);

    boolean visit(OracleCursorExpr x);

    void endVisit(OracleCursorExpr x);

    boolean visit(OracleIsSetExpr x);

    void endVisit(OracleIsSetExpr x);

    boolean visit(ModelClause.ReturnRowsClause x);

    void endVisit(ModelClause.ReturnRowsClause x);

    boolean visit(ModelClause.MainModelClause x);

    void endVisit(ModelClause.MainModelClause x);

    boolean visit(ModelClause.ModelColumnClause x);

    void endVisit(ModelClause.ModelColumnClause x);

    boolean visit(ModelClause.QueryPartitionClause x);

    void endVisit(ModelClause.QueryPartitionClause x);

    boolean visit(ModelClause.ModelColumn x);

    void endVisit(ModelClause.ModelColumn x);

    boolean visit(ModelClause.ModelRulesClause x);

    void endVisit(ModelClause.ModelRulesClause x);

    boolean visit(ModelClause.CellAssignmentItem x);

    void endVisit(ModelClause.CellAssignmentItem x);

    boolean visit(ModelClause.CellAssignment x);

    void endVisit(ModelClause.CellAssignment x);

    boolean visit(ModelClause x);

    void endVisit(ModelClause x);

    boolean visit(OracleReturningClause x);

    void endVisit(OracleReturningClause x);

    boolean visit(OracleInsertStatement x);

    void endVisit(OracleInsertStatement x);

    boolean visit(InsertIntoClause x);

    void endVisit(InsertIntoClause x);

    boolean visit(OracleMultiInsertStatement x);

    void endVisit(OracleMultiInsertStatement x);

    boolean visit(ConditionalInsertClause x);

    void endVisit(ConditionalInsertClause x);

    boolean visit(ConditionalInsertClauseItem x);

    void endVisit(ConditionalInsertClauseItem x);

    boolean visit(OracleSelectQueryBlock x);

    void endVisit(OracleSelectQueryBlock x);

    boolean visit(OracleLockTableStatement x);

    void endVisit(OracleLockTableStatement x);

    boolean visit(OracleAlterSessionStatement x);

    void endVisit(OracleAlterSessionStatement x);

    boolean visit(OracleExprStatement x);

    void endVisit(OracleExprStatement x);

    boolean visit(OracleDatetimeExpr x);

    void endVisit(OracleDatetimeExpr x);

    boolean visit(OracleSysdateExpr x);

    void endVisit(OracleSysdateExpr x);

    boolean visit(OracleExceptionStatement x);

    void endVisit(OracleExceptionStatement x);

    boolean visit(OracleExceptionStatement.Item x);

    void endVisit(OracleExceptionStatement.Item x);

    boolean visit(OracleArgumentExpr x);

    void endVisit(OracleArgumentExpr x);

    boolean visit(OracleSetTransactionStatement x);

    void endVisit(OracleSetTransactionStatement x);

    boolean visit(OracleExplainStatement x);

    void endVisit(OracleExplainStatement x);

    boolean visit(OracleAlterProcedureStatement x);

    void endVisit(OracleAlterProcedureStatement x);

    boolean visit(OracleAlterTableDropPartition x);

    void endVisit(OracleAlterTableDropPartition x);

    boolean visit(OracleAlterTableTruncatePartition x);

    void endVisit(OracleAlterTableTruncatePartition x);

    boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x);

    void endVisit(OracleAlterTableSplitPartition.TableSpaceItem x);

    boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x);

    void endVisit(OracleAlterTableSplitPartition.UpdateIndexesClause x);

    boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x);

    void endVisit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x);

    boolean visit(OracleAlterTableSplitPartition x);

    void endVisit(OracleAlterTableSplitPartition x);

    boolean visit(OracleAlterTableModify x);

    void endVisit(OracleAlterTableModify x);

    boolean visit(OracleCreateIndexStatement x);

    void endVisit(OracleCreateIndexStatement x);

    boolean visit(OracleForStatement x);

    void endVisit(OracleForStatement x);

    boolean visit(OracleRangeExpr x);

    void endVisit(OracleRangeExpr x);

    boolean visit(OracleAlterIndexStatement x);

    void endVisit(OracleAlterIndexStatement x);

    boolean visit(OraclePrimaryKey x);

    void endVisit(OraclePrimaryKey x);

    boolean visit(OracleCreateTableStatement x);

    void endVisit(OracleCreateTableStatement x);

    boolean visit(OracleAlterIndexStatement.Rebuild x);

    void endVisit(OracleAlterIndexStatement.Rebuild x);

    boolean visit(OracleStorageClause x);

    void endVisit(OracleStorageClause x);

    boolean visit(OracleGotoStatement x);

    void endVisit(OracleGotoStatement x);

    boolean visit(OracleLabelStatement x);

    void endVisit(OracleLabelStatement x);

    boolean visit(OracleAlterTriggerStatement x);

    void endVisit(OracleAlterTriggerStatement x);

    boolean visit(OracleAlterSynonymStatement x);

    void endVisit(OracleAlterSynonymStatement x);

    boolean visit(OracleAlterViewStatement x);

    void endVisit(OracleAlterViewStatement x);

    boolean visit(OracleAlterTableMoveTablespace x);

    void endVisit(OracleAlterTableMoveTablespace x);

    boolean visit(OracleSizeExpr x);

    void endVisit(OracleSizeExpr x);

    boolean visit(OracleFileSpecification x);

    void endVisit(OracleFileSpecification x);

    boolean visit(OracleAlterTablespaceAddDataFile x);

    void endVisit(OracleAlterTablespaceAddDataFile x);

    boolean visit(OracleAlterTablespaceStatement x);

    void endVisit(OracleAlterTablespaceStatement x);

    boolean visit(OracleExitStatement x);

    void endVisit(OracleExitStatement x);

    boolean visit(OracleContinueStatement x);

    void endVisit(OracleContinueStatement x);

    boolean visit(OracleRaiseStatement x);

    void endVisit(OracleRaiseStatement x);

    boolean visit(OracleCreateDatabaseDbLinkStatement x);

    void endVisit(OracleCreateDatabaseDbLinkStatement x);

    boolean visit(OracleDropDbLinkStatement x);

    void endVisit(OracleDropDbLinkStatement x);

    boolean visit(OracleDataTypeTimestamp x);

    void endVisit(OracleDataTypeTimestamp x);

    boolean visit(OracleDataTypeIntervalYear x);

    void endVisit(OracleDataTypeIntervalYear x);

    boolean visit(OracleDataTypeIntervalDay x);

    void endVisit(OracleDataTypeIntervalDay x);

    boolean visit(OracleUsingIndexClause x);

    void endVisit(OracleUsingIndexClause x);

    boolean visit(OracleLobStorageClause x);

    void endVisit(OracleLobStorageClause x);

    boolean visit(OracleUnique x);

    void endVisit(OracleUnique x);

    boolean visit(OracleForeignKey x);

    void endVisit(OracleForeignKey x);

    boolean visit(OracleCheck x);

    void endVisit(OracleCheck x);

    boolean visit(OracleSupplementalIdKey x);

    void endVisit(OracleSupplementalIdKey x);

    boolean visit(OracleSupplementalLogGrp x);

    void endVisit(OracleSupplementalLogGrp x);

    boolean visit(OracleCreateTableStatement.Organization x);

    void endVisit(OracleCreateTableStatement.Organization x);

    boolean visit(OracleCreateTableStatement.OracleExternalRecordFormat x);

    void endVisit(OracleCreateTableStatement.OracleExternalRecordFormat x);

    boolean visit(OracleCreateTableStatement.OIDIndex x);

    void endVisit(OracleCreateTableStatement.OIDIndex x);

    boolean visit(OracleCreatePackageStatement x);

    void endVisit(OracleCreatePackageStatement x);

    boolean visit(OracleExecuteImmediateStatement x);

    void endVisit(OracleExecuteImmediateStatement x);
}
