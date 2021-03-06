package org.the.force.jdbc.partition.engine.stmt.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.RsIndexEvaluator;
import org.the.force.jdbc.partition.engine.stmt.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.stmt.SqlRefer;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * select的定义
 */
public class SelectTable {


    //被引用的立场
    private final ConditionalSqlTable sqlTable;

    private final boolean distinctAll;

    private List<SqlExprEvaluator> selectValueNodes = new ArrayList<>();

    private List<String> selectLabels = new ArrayList<>();//总是有值的，不管SQLSelectItem是否有alias,但是并不作为输出的SQLSelectItem

    //非allColumnItems的selectItem,包括添加进去的，最终重置到查询中的sqlItem是normalSelectItems+allColumnItems
    private List<SQLSelectItem> normalSelectItems = new ArrayList<>();

    //select * 或t.*的item
    private List<SQLSelectItem> allColumnItems = new ArrayList<>();

    /**
     * 一个label可能映射到多个列上，映射关系
     */
    private Map<String, int[]> labelMap = new HashMap<>();

    //client查询的实际长度
    private int queryBound;

    //group by  依赖有序但是顺序的方向不敏感
    private GroupBy groupBy;

    //依赖顺序但是对顺序的方向不敏感

    private GroupBy distinctAllGroupBy;

    //优先级最低  正序还是倒序敏感，groupBy和distinctAllGroupBy由于对字段先后和方向不敏感，因此尽量满足order by的要求
    //client指定的排序，固定不变，但是可以通过数据库实现也可以通过jdbc实现，因此输出的sql可变
    private OrderBy orderBy;
    //优先级最低
    private ResultLimit resultLimit;

    public SelectTable(ConditionalSqlTable sqlTable, boolean distinctAll) {
        this.sqlTable = sqlTable;
        this.distinctAll = distinctAll;
    }

    /**
     * label的规则
     *
     * @param sqlExprEvaluator
     * @param sqlSelectItem
     */
    public void addValueNode(SQLSelectItem sqlSelectItem, SqlExprEvaluator sqlExprEvaluator) {
        if (getIndex(sqlExprEvaluator) > -1) {
            throw new SqlParseException("sqlSelectItem已经存在");
        }
        String label = sqlSelectItem.getAlias();
        if (label == null) {
            if (sqlExprEvaluator instanceof SqlRefer) {
                label = ((SqlRefer) sqlExprEvaluator).getName();
            }
        }
        selectLabels.add(label);//可以为空
        selectValueNodes.add(sqlExprEvaluator);
        normalSelectItems.add(sqlSelectItem);
        if (label != null) {
            String key = label.toLowerCase();
            int[] list = labelMap.get(key);
            if (list == null) {
                list = new int[] {selectLabels.size() - 1};
                labelMap.put(key, list);
            } else {
                int[] newArray = new int[list.length + 1];
                System.arraycopy(list, 0, newArray, 0, list.length);
                newArray[list.length] = selectLabels.size() - 1;
                labelMap.put(key, newArray);
            }
        }
    }

    public void addAllColumnItem(SQLSelectItem item) {
        allColumnItems.add(item);
    }


    public int getIndex(SqlExprEvaluator target) {
        if (target instanceof SqlRefer) {
            SqlRefer sqlRefer = (SqlRefer) target;
            String ownerName = sqlRefer.getOwnerName();
            if (ownerName == null || ownerName.equals(sqlTable.getAlias()) || ownerName.equalsIgnoreCase(sqlTable.getTableName())) {
                int index = getIndex(sqlRefer.getName());
                if (index > -1) {
                    return index;
                }
            }
        } else if (target instanceof RsIndexEvaluator) {
            RsIndexEvaluator rsIndexEvaluator = ((RsIndexEvaluator) target);
            if (rsIndexEvaluator.getSqlTable() == this.sqlTable) {
                return rsIndexEvaluator.getIndex();
            } else {
                return -1;
            }
        }
        int size = selectValueNodes.size();
        for (int i = 0; i < size; i++) {
            SqlExprEvaluator sqlExprEvaluator = selectValueNodes.get(i);
            if (target.getOriginalSqlExpr().equals(sqlExprEvaluator.getOriginalSqlExpr())) {
                return i;
            }
        }
        return -1;
    }

    public int getIndex(String label) {
        int[] array = labelMap.get(label.toLowerCase());
        if (array == null) {
            return -1;
        }
        if (array.length == 1) {
            return array[0];
        }
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < queryBound) {
                count++;
            }
        }
        if (count > 1) {
            throw new SqlParseException(label + "指向多列，无法确定引用哪一列");
        }
        return array[0];
    }

    public boolean checkEquals(SqlExprEvaluator left, SqlExprEvaluator right) {
        int index1 = getIndex(left);
        int index2 = getIndex(right);
        return index1 > -1 && index2 > -1 && index1 == index2;
    }

    public int getNormalValueNodeSize() {
        return selectValueNodes.size();
    }


    /**
     * 更新结果集求值方式
     *
     * @param index
     * @param sqlExprEvaluator
     */

    public void updateSqlExprEvaluator(int index, SqlExprEvaluator sqlExprEvaluator) {
        updateSqlExprEvaluator(index, sqlExprEvaluator, null);
    }


    public void updateSqlExprEvaluator(int index, SqlExprEvaluator sqlExprEvaluator, SQLExpr newSQLExpr) {
        if (!sqlExprEvaluator.getOriginalSqlExpr().equals(selectValueNodes.get(index).getOriginalSqlExpr())) {
            throw new RuntimeException("!sqlExprEvaluator.getOriginalSqlExpr().equals(selectValueNodes.get(index).getOriginalSqlExpr())");
        }
        if (newSQLExpr != null) {
            getNormalSelectItem(index).setExpr(newSQLExpr);
        }
        selectValueNodes.set(index, sqlExprEvaluator);
    }


    public SqlExprEvaluator getSelectValueNode(int index) {
        return selectValueNodes.get(index);
    }

    public SQLSelectItem getNormalSelectItem(int index) {
        return normalSelectItems.get(index);
    }


    public String getSelectLabel(int index) {
        return selectLabels.get(index);
    }


    public List<SQLSelectItem> getAllColumnItems() {
        return allColumnItems;
    }

    public int getQueryBound() {
        return queryBound;
    }

    public void setQueryBound(int queryBound) {
        this.queryBound = queryBound;
    }

    //==========================

    public ConditionalSqlTable getSqlTable() {
        return sqlTable;
    }

    public boolean isDistinctAll() {
        return distinctAll;
    }

    //==================

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public GroupBy getDistinctAllGroupBy() {
        return distinctAllGroupBy;
    }

    public void setDistinctAllGroupBy(GroupBy distinctAllGroupBy) {
        this.distinctAllGroupBy = distinctAllGroupBy;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public ResultLimit getResultLimit() {
        return resultLimit;
    }

    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }


    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public void setResultLimit(ResultLimit resultLimit) {
        this.resultLimit = resultLimit;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SelectTable that = (SelectTable) o;

        return getSqlTable().equals(that.getSqlTable());

    }

    @Override
    public int hashCode() {
        return getSqlTable().hashCode();
    }



}
