package org.the.force.jdbc.partition.driver.result;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.engine.executor.result.UpdateMerger;
import org.the.force.jdbc.partition.engine.stmt.impl.ParamLineStmt;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/29.
 * {@link ParamLineStmt}
 */
public class UpdateResult implements PResult {

    private final LogicDbConfig logicDbConfig;

    private final UpdateMerger updateMerger;//将MultiLogicSql的结果进行了归集

    protected ResultSet generatedKeysResults;//将MultiLogicSql的结果进行了归集


    public UpdateResult(LogicDbConfig logicDbConfig, UpdateMerger updateMerger) {
        this.logicDbConfig = logicDbConfig;
        this.updateMerger = updateMerger;
    }


    public int getUpdateCount() throws SQLException {
        return updateMerger.toInt();
    }

    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return generatedKeysResults;
    }

    public void close() throws SQLException {
        if (generatedKeysResults != null) {
            generatedKeysResults.close();
        }
    }

    public boolean getMoreResults() throws SQLException {
        return false;
    }

    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    public UpdateMerger getUpdateMerger() {
        return updateMerger;
    }
}
