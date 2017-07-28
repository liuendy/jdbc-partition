package org.the.force.jdbc.partition.engine.executor;

import org.the.force.jdbc.partition.engine.executor.result.UpdateMerger;
import org.the.force.jdbc.partition.resource.connection.ConnectionAdapter;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xuji on 2017/6/2.
 */
public abstract class WriteSqlExecutionCommand extends SqlExecutionResource {

    private static Log logger = LogFactory.getLog(WriteSqlExecutionCommand.class);

    private final UpdateMerger updateMerger;

    public WriteSqlExecutionCommand(ConnectionAdapter connectionAdapter, ThreadPoolExecutor threadPool, ExecutorConfig executorConfig, UpdateMerger updateMerger) {
        super(connectionAdapter, threadPool, executorConfig);
        this.updateMerger = updateMerger;
    }

    public boolean returnGeneralKeys() {
        return false;
    }

    public abstract int[] invokeWrite(Statement statement, String sql, List<Integer> lineNumMap) throws SQLException;

    public void collectResult(List<Integer> lineNumMap, int[] result, Statement statement) {
        for (int i = 0; i < result.length; i++) {
            if (result[i] < 0) {
                updateMerger.addFailed(lineNumMap.get(i), result[i]);
            } else {
                updateMerger.addSuccess(lineNumMap.get(i), result[i]);
            }
        }
        if (returnGeneralKeys()) {

        }
    }
}