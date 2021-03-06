package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.engine.executor.ast.DeleteAst;
import org.the.force.jdbc.partition.engine.executor.ast.InsertAst;
import org.the.force.jdbc.partition.engine.executor.ast.MyExecutableReplaceIntoAst;
import org.the.force.jdbc.partition.engine.executor.ast.UpdateAst;
import org.the.force.jdbc.partition.engine.executor.SqlExecutor;
import org.the.force.jdbc.partition.engine.executor.ast.TableDdlAst;
import org.the.force.jdbc.partition.engine.executor.dql.factory.BlockQueryExecutorFactory;
import org.the.force.jdbc.partition.engine.executor.dql.factory.UnionQueryExecutorFactory;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDeleteStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropTableStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUpdateStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/5/17.
 * sqlExecutor匹配器
 */
public class SqlExecutorFactory extends AbstractVisitor {

    private static Log logger = LogFactory.getLog(SqlExecutorFactory.class);

    protected final LogicDbConfig logicDbConfig;

    private final SqlObjCopier sqlObjCopier;//用于复制statement时重置子查询,在进入主解析流程之前就重置掉子查询，让后续的流程基本不必关心子查询的处理，简化逻辑

    private SQLObject sqlStatement;

    private SQLExprTableSource tableSource;

    private Constructor constructor;


    public SqlExecutorFactory(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
        sqlObjCopier = new SqlObjCopier(logicDbConfig);
    }


    protected boolean isContinue() {
        return constructor == null;
    }


    public SqlExecutor getSqlExecutor() {
        List<Object> args = new ArrayList<>();
        args.add(logicDbConfig);
        args.add(sqlStatement);
        if (tableSource != null) {
            args.add(tableSource);
        }
        try {
            Object obj = constructor.newInstance(args.toArray());
            if (obj instanceof QueryExecutorFactory) {
                return ((QueryExecutorFactory) obj).buildQueryExecutor();
            } else if (obj instanceof SqlExecutor) {
                return (SqlExecutor) obj;
            }
            return null;
        } catch (Exception e) {
            throw new SqlParseException(e);
        }
    }

    //==========================mysql=============================

    /**
     * @param x
     * @return
     */
    public boolean visit(MySqlSelectQueryBlock x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlSelectQueryBlock:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            try {
                constructor = BlockQueryExecutorFactory.class.getConstructor(LogicDbConfig.class, SQLSelectQueryBlock.class);
                sqlStatement = sqlObjCopier.copy(x);
            } catch (NoSuchMethodException e) {
                throw new SqlParseException("", e);
            }
        }
        return isContinue();
    }

    /**
     * @param x
     * @return
     */
    public boolean visit(SQLUnionQuery x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlUnionQuery:{}", SQLUtils.toSQLString(mySqlUnionQuery, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            try {
                constructor = UnionQueryExecutorFactory.class.getConstructor(LogicDbConfig.class, SQLUnionQuery.class);
                sqlStatement = sqlObjCopier.copy(x);
            } catch (NoSuchMethodException e) {
                throw new SqlParseException("", e);
            }
        }
        return isContinue();
    }

    /**
     * insert
     *
     * @param x
     * @return
     */
    public boolean visit(MySqlInsertStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlInsertStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        try {
            if (sqlStatement == null) {
                constructor = InsertAst.class.getConstructor(LogicDbConfig.class, SQLInsertStatement.class);
                sqlStatement = sqlObjCopier.copy(x);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContinue();
    }

    public boolean visit(MySqlReplaceStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlReplaceStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        try {
            if (sqlStatement == null) {
                constructor = MyExecutableReplaceIntoAst.class.getConstructor(LogicDbConfig.class, MySqlReplaceStatement.class);
                sqlStatement = sqlObjCopier.copy(x);
            }
        } catch (Exception e) {
            throw new SqlParseException("", e);
        }
        return isContinue();
    }

    /**
     * physic
     *
     * @param x
     * @return
     */
    public boolean visit(MySqlUpdateStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlUpdateStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        try {
            if (sqlStatement == null) {
                constructor = UpdateAst.class.getConstructor(LogicDbConfig.class, SQLUpdateStatement.class);
                sqlStatement = sqlObjCopier.copy(x);
            }
        } catch (Exception e) {
            throw new SqlParseException("", e);
        }
        return isContinue();
    }

    /**
     * delete
     *
     * @param x
     * @return
     */
    public boolean visit(MySqlDeleteStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlDeleteStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        try {
            if (sqlStatement == null) {
                constructor = DeleteAst.class.getConstructor(LogicDbConfig.class, SQLDeleteStatement.class);
                sqlStatement = sqlObjCopier.copy(x);
            }
        } catch (Exception e) {
            throw new SqlParseException("", e);
        }
        return isContinue();
    }

    /**
     * create executor
     *
     * @param x
     * @return
     */
    public boolean visit(MySqlCreateTableStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlCreateTableStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            sqlStatement = x;
            tableSource = x.getTableSource();
            try {
                constructor = TableDdlAst.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                throw new SqlParseException("", e);
            }
        }
        return isContinue();
    }
    //==========================mysql=============================

    /**
     * drop executor
     *
     * @param x
     * @return
     */
    public boolean visit(SQLDropTableStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("SQLDropTableStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            sqlStatement = x;
            tableSource = x.getTableSources().get(0);
            try {
                constructor = TableDdlAst.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                throw new SqlParseException("", e);
            }
        }
        return isContinue();
    }

    /**
     * @param x
     * @return
     */
    public boolean visit(SQLAlterTableStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("SQLAlterTableStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            tableSource = x.getTableSource();
            sqlStatement = x;
            try {
                constructor = TableDdlAst.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                throw new SqlParseException("", e);
            }
        }
        return isContinue();
    }
}
