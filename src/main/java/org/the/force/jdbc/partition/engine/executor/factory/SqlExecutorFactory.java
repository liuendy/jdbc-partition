package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.engine.executor.ddl.TableDdlExecutor;
import org.the.force.jdbc.partition.engine.executor.dml.DeleteExecutor;
import org.the.force.jdbc.partition.engine.executor.dml.InsertExecutor;
import org.the.force.jdbc.partition.engine.executor.dml.MySqlReplaceIntoExecutor;
import org.the.force.jdbc.partition.engine.executor.dml.UpdateExecutor;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.executor.SqlExecutor;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;
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
import org.the.force.thirdparty.druid.sql.parser.ParserException;
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

    private SQLObject sqlStatement;

    private SQLExprTableSource tableSource;

    private Constructor constructor;


    public SqlExecutorFactory(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }


    protected boolean isContinue() {
        return constructor == null;
    }

    public boolean visit(SQLVariantRefExpr x) {
        return false;
    }

    public SqlExecutor getSqlPlan() {
        List<Object> args = new ArrayList<>();
        args.add(logicDbConfig);
        args.add(sqlStatement);
        if (tableSource != null) {
            args.add(tableSource);
        }
        try {
            Object obj = constructor.newInstance(args.toArray());
            if (obj instanceof SqlExecutor) {
                return (SqlExecutor) obj;
            }
            if(obj instanceof QueryExecutorFactory){
                return ((QueryExecutorFactory)obj).build();
            }
            //TODO check null
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                sqlStatement = x;
            } catch (NoSuchMethodException e) {
                throw new ParserException("", e);
            }
        }
        return isContinue();
    }

    /**
     * @param mySqlUnionQuery
     * @return
     */
    public boolean visit(SQLUnionQuery mySqlUnionQuery) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlUnionQuery:{}", SQLUtils.toSQLString(mySqlUnionQuery, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            try {
                constructor = UnionQueryExecutorFactory.class.getConstructor(LogicDbConfig.class, SQLUnionQuery.class);
                sqlStatement = mySqlUnionQuery;
            } catch (NoSuchMethodException e) {
                throw new ParserException("", e);
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
                constructor = InsertExecutor.class.getConstructor(LogicDbConfig.class, SQLInsertStatement.class);
                sqlStatement = x;
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
                constructor = MySqlReplaceIntoExecutor.class.getConstructor(LogicDbConfig.class, MySqlReplaceStatement.class);
                sqlStatement = x;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                constructor = UpdateExecutor.class.getConstructor(LogicDbConfig.class, SQLUpdateStatement.class);
                sqlStatement = x;
                sqlStatement = x;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                constructor = DeleteExecutor.class.getConstructor(LogicDbConfig.class, SQLDeleteStatement.class);
                sqlStatement = x;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContinue();
    }

    /**
     * create blockquery
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
                constructor = TableDdlExecutor.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return isContinue();
    }
    //==========================mysql=============================

    /**
     * drop blockquery
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
                constructor = TableDdlExecutor.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
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
                constructor = TableDdlExecutor.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return isContinue();
    }
}
